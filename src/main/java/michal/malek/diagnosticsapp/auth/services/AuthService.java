package michal.malek.diagnosticsapp.auth.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.auth.exceptions.UserWithEmailAlreadyExist;
import michal.malek.diagnosticsapp.auth.models.*;
import michal.malek.diagnosticsapp.auth.repositories.RetrievePasswordOperationRepository;
import michal.malek.diagnosticsapp.auth.repositories.UserRepository;
import michal.malek.diagnosticsapp.core.mappers.UserMapper;
import michal.malek.diagnosticsapp.core.models.AppResponse;
import michal.malek.diagnosticsapp.core.models.ResponseType;
import michal.malek.diagnosticsapp.core.models.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RetrievePasswordOperationRepository retrievePasswordOperationRepository;
    private final PasswordEncoder passwordEncoder;
    private final CookieService cookieService;
    private final JWTService jwtService;
    private final EmailService emailService;
    private final GoogleOauth2Service googleOauth2Service;

    @Value("${jwt.exp}")
    private int jwtExp;
    @Value("${jwt.refresh.exp}")
    private int jwtRefreshExp;

    public String loginWithGoogle(String code, HttpServletResponse response, RedirectAttributes redirectAttributes){
        try{
            OAuth2AccessToken accessToken = googleOauth2Service.codeToAccessToken(code);
            OAuth2AuthenticationToken authentication = googleOauth2Service.accessTokenToAuthToken(accessToken);
            OAuth2User user = authentication.getPrincipal();
            try {
                if(authentication.isAuthenticated()){
                    String email = user.getAttribute("email");
                    UserEntity userByEmail = userRepository.findByEmail(email);

                    if(userByEmail==null){
                        UserEntity newUser = new UserEntity(email,null);
                        newUser.setGoogle(true);
                        newUser.setEnabled(true);

                        userRepository.save(newUser);
                        userByEmail = newUser;
                    }

                    if(!userByEmail.isGoogle()){
                        redirectAttributes.addFlashAttribute("message", new AppResponse("Account already created with this email", ResponseType.NOTIFICATION));
                        return "redirect:/login";
                    }

                    UserType userType = userByEmail.getUserType();
                    String uid = userByEmail.getUid();

                    String tokenValue = jwtService.generateToken(uid,userType.toString(),email, jwtExp);
                    String refreshValue = jwtService.generateToken(uid,userType.toString(),email, jwtRefreshExp);
                    Cookie token = cookieService.generateCookie("token", tokenValue, jwtExp);
                    Cookie refresh = cookieService.generateCookie("refreshToken", refreshValue, jwtRefreshExp);
                    response.addCookie(token);
                    response.addCookie(refresh);

                    redirectAttributes.addFlashAttribute("message", new AppResponse("Welcome Back", ResponseType.SUCCESS));
                    return "redirect:/home";

                }else {
                    redirectAttributes.addFlashAttribute("message",new AppResponse("AUTHORIZATION WENT WRONG 1",ResponseType.FAILURE));
                    return "redirect:/login";
                }
            }catch( AuthenticationException authenticationException){
                redirectAttributes.addFlashAttribute("message",new AppResponse("AUTHORIZATION WENT WRONG 2",ResponseType.FAILURE));
                return "redirect:/login";
            }

        }catch (Exception e){
            redirectAttributes.addFlashAttribute("message",new AppResponse("AUTHORIZATION WENT WRONG",ResponseType.FAILURE));
            return "redirect:/login";
        }


    }

    public String loginTemplate(Model model) {
        String googleUrl = googleOauth2Service.getGoogleOAuth2redirectUrl();
        model.addAttribute("googleUrl", googleUrl);
        return "auth/login";
    }

    public String login ( String email, String password, HttpServletResponse response, RedirectAttributes redirectAttributes ){

        UserEntity byEmail = userRepository.findByEmail(email);
        if(byEmail != null){
            if(byEmail.isGoogle()){
                redirectAttributes.addFlashAttribute("message", new AppResponse("Please login with google", ResponseType.NOTIFICATION));
                return "redirect:/login";
            }
            if(byEmail.isEnabled()){
                if(passwordEncoder.matches(password, byEmail.getPassword())){
                    String uid = byEmail.getUid();
                    String userEmail = byEmail.getEmail();
                    response.addCookie( cookieService.generateCookie("token", jwtService.generateToken(uid, byEmail.getUserType().toString(), userEmail, jwtExp), jwtExp));
                    response.addCookie( cookieService.generateCookie("refreshToken", jwtService.generateToken(uid, byEmail.getUserType().toString(), userEmail,jwtRefreshExp) , jwtRefreshExp));

                    redirectAttributes.addFlashAttribute("message", new AppResponse("Welcome Back", ResponseType.SUCCESS));
                    return "redirect:/home";
                }
            }else {
                redirectAttributes.addFlashAttribute("message", new AppResponse("Please Activate your account via email", ResponseType.NOTIFICATION));
                return "redirect:/login";
            }
        }
        redirectAttributes.addFlashAttribute("message", new AppResponse("Login Failed", ResponseType.FAILURE));
        return "redirect:/login";
    }

    public String register(UserRegisterDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            String errorMessage = String.valueOf(bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .findFirst()
                    .orElse(null)
            );
            if(errorMessage == null){errorMessage = "Something went wrong";}
            redirectAttributes.addFlashAttribute("message", new AppResponse(errorMessage, ResponseType.FAILURE));
            return "redirect:/register";
        }

        boolean isGood;
        try{
             isGood = this.registerValidate(dto);
        }catch (UserWithEmailAlreadyExist e){
            redirectAttributes.addFlashAttribute("message", new AppResponse("Account with this email already exist", ResponseType.FAILURE));
            return "redirect:/register";
        }

        if(isGood){
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
            UserEntity userEntity = UserMapper.INSTANCE.userRegisterDTOToUserEntity(dto);
            try {
                emailService.sendActivation(userEntity);
            }catch (Exception e){
                System.out.println(e.getMessage());
                redirectAttributes.addFlashAttribute("message", new AppResponse("Something went wrong while sending activation email", ResponseType.ERROR));
                return "redirect:/register";
            }

            userRepository.saveAndFlush(userEntity);
            redirectAttributes.addFlashAttribute("message", new AppResponse("Confirm email on your inbox", ResponseType.SUCCESS));
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("message", new AppResponse("Passwords dont match", ResponseType.FAILURE));
        return "redirect:/register";
    }

    private boolean registerValidate(UserRegisterDTO dto){
        UserEntity byEmail = userRepository.findByEmail(dto.getEmail());
        if(byEmail != null){
            throw new UserWithEmailAlreadyExist();
        }
        return dto.getPassword().equals(dto.getPassword2());
    }

    public String activateAccount(String uid, RedirectAttributes redirectAttributes) {
        UserEntity byUid = userRepository.findByUid(uid);
        if(byUid != null){
            byUid.setEnabled(true);
            userRepository.saveAndFlush(byUid);
            redirectAttributes.addFlashAttribute("message", new AppResponse("Account activated", ResponseType.SUCCESS));
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("message", new AppResponse("Activation went wrong please contact support", ResponseType.ERROR));
        return "redirect:/login";
    }

    @Transactional
    public String retrievePassword(String email, RedirectAttributes redirectAttributes) {
        UserEntity byEmail = userRepository.findByEmail(email);
        if(byEmail != null){

            if(byEmail.isGoogle()){
                redirectAttributes.addFlashAttribute("message", new AppResponse("Account with this email does not exist", ResponseType.FAILURE));
                return "redirect:/login";
            }
            if(!byEmail.isEnabled()){
                redirectAttributes.addFlashAttribute("message", new AppResponse("Please first activate your account", ResponseType.ERROR));
                return "redirect:/login";
            }

            String userUid = byEmail.getUid();

            List<RetrievePasswordOperation> byUserUid = retrievePasswordOperationRepository.findByUserUid(userUid);
            if(!byUserUid.isEmpty()){
                retrievePasswordOperationRepository.deleteAllByUserUid(userUid);
            }

            RetrievePasswordOperation retrievePasswordOperation = new RetrievePasswordOperation(byEmail.getUid());
            retrievePasswordOperationRepository.saveAndFlush(retrievePasswordOperation);

            try {
                emailService.sendPasswordRecovery(byEmail ,retrievePasswordOperation.getUid());
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", new AppResponse("We cant send you email, please contact support", ResponseType.ERROR));
                return "redirect:/retrieve-password";
            }
            redirectAttributes.addFlashAttribute("message", new AppResponse("Check your inbox in order to reset password", ResponseType.NOTIFICATION));
            return "redirect:/login";

        }else {
            redirectAttributes.addFlashAttribute("message", new AppResponse("Account with this email does not exist", ResponseType.FAILURE));
            return "redirect:/retrieve-password";
        }

    }

    @Transactional
    public String resetPassword(ResetPasswordDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        String operationUid = dto.getOperationUid();
        if (operationUid == null || operationUid.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", new AppResponse("Something went wrong while getting uid please contact support", ResponseType.ERROR));
            return "redirect:/login";
        }

        RetrievePasswordOperation operation = retrievePasswordOperationRepository.findByUid(operationUid);
        if (operation == null) {
            redirectAttributes.addFlashAttribute("message", new AppResponse("Operation is outdated, please start over", ResponseType.ERROR));
            return "redirect:/login";
        }

        UserEntity user = userRepository.findByUid(operation.getUserUid());
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", new AppResponse("Something went wrong while getting user from uid please contact support", ResponseType.ERROR));
            return "redirect:/login";
        }

        List<RetrievePasswordOperation> byUserUid = retrievePasswordOperationRepository.findByUserUid(user.getUid());
        if(byUserUid==null || byUserUid.isEmpty()){
            redirectAttributes.addFlashAttribute("message", new AppResponse("Reset operation is outdated", ResponseType.NOTIFICATION));
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            String errorMessage = String.valueOf(bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .findFirst()
                    .orElse(null)
            );
            if(errorMessage == null){errorMessage = "Something went wrong";}
            redirectAttributes.addFlashAttribute("message", new AppResponse(errorMessage, ResponseType.FAILURE));
            return "redirect:/reset-password?operationUid=" + operationUid;
        }

        if(!dto.getPassword().equals(dto.getPassword2())){
            redirectAttributes.addFlashAttribute("message", new AppResponse("Passwords dont match", ResponseType.FAILURE));
            return "redirect:/reset-password?operationUid=" + operationUid;
        }

        if((passwordEncoder.matches(dto.getPassword(), user.getPassword()))){
            redirectAttributes.addFlashAttribute("message", new AppResponse("Password cannot be the same as old one", ResponseType.FAILURE));
            return "redirect:/reset-password?operationUid=" + operationUid;
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.saveAndFlush(user);
        retrievePasswordOperationRepository.deleteAllByUserUid(user.getUid());
        redirectAttributes.addFlashAttribute("message", new AppResponse("Password changed", ResponseType.SUCCESS));
        return "redirect:/login";
    }

    public String resetPasswordTemplate(String operationUid, Model model) {
        model.addAttribute("resetPasswordDTO", new ResetPasswordDTO(operationUid));
        return "auth/reset-password";
    }

    @Transactional
    public void deleteNotActivatedAccounts() {
        userRepository.deleteAllByEnabledFalse();
    }

    @Transactional
    public void deleteOutdatedResetOperations() {
        List<RetrievePasswordOperation> all = retrievePasswordOperationRepository.findAll();
        if (all == null || all.isEmpty()) {
            return;
        }

        List<RetrievePasswordOperation> outdatedOperations = all.stream()
                .filter(operation -> Duration.between(operation.getDate().toInstant(), new Date().toInstant()).toMinutes() >= 5)
                .collect(Collectors.toList());

        retrievePasswordOperationRepository.deleteAll(outdatedOperations);
    }

}
