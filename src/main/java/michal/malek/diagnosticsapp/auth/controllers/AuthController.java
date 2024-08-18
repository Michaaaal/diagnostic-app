package michal.malek.diagnosticsapp.auth.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.auth.models.ResetPasswordDTO;
import michal.malek.diagnosticsapp.auth.models.UserRegisterDTO;
import michal.malek.diagnosticsapp.auth.services.AuthService;
import michal.malek.diagnosticsapp.auth.services.JWTService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller()
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JWTService jwtService;


    @GetMapping("/login")
    public String login(Model model) {
        return authService.loginTemplate(model);
    }

    @GetMapping("/auth-callback")
    public String authCallback(@RequestParam Map<String, String> allParams, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String code = allParams.get("code");
        return authService.loginWithGoogle(code, response, redirectAttributes);
    }

    @GetMapping("/retrieve-password")
    public String resetPassword() {
        return "auth/retrieve-password";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userRegisterDTO", new UserRegisterDTO());
        return "auth/register";
    }

    @GetMapping("/home")
    public String home(@CookieValue(name = "refreshToken", required = false) String refreshToken, Model model){
        model.addAttribute("userEmail", jwtService.getSubject(refreshToken));
        return "auth/home";
    }

    @PostMapping("/register-post")
    public String registerPost(@ModelAttribute @Valid UserRegisterDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        return authService.register(dto, bindingResult, redirectAttributes);
    }

    @PostMapping("/login-post")
    public String login(@RequestParam String email, @RequestParam String password, HttpServletResponse response, RedirectAttributes redirectAttributes){
        return authService.login(email, password, response, redirectAttributes);
    }

    @GetMapping("/account-activate")
    public String activateAccount(@RequestParam String uid, RedirectAttributes redirectAttributes){
        return authService.activateAccount(uid, redirectAttributes);
    }

    @GetMapping("/retrieve-password-start")
    public String retrievePassword(@RequestParam String email, RedirectAttributes redirectAttributes){
        return authService.retrievePassword(email, redirectAttributes);
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam String operationUid, Model model){
        return authService.resetPasswordTemplate(operationUid, model);
    }

    @PostMapping("/reset-password-post")
    public String resetPassword(@ModelAttribute @Valid ResetPasswordDTO dto, BindingResult bindingResult , RedirectAttributes redirectAttributes){
        return authService.resetPassword(dto, bindingResult, redirectAttributes);
    }

    @Scheduled(cron = "0 0 5 * * ?")
    public void deleteNotActivatedAccounts(){
        authService.deleteNotActivatedAccounts();
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void deleteOutdatedResetOperations(){
        authService.deleteOutdatedResetOperations();
    }

}
