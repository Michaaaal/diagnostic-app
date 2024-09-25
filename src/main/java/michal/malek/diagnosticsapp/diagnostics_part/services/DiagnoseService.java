package michal.malek.diagnosticsapp.diagnostics_part.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.auth.models.UserType;
import michal.malek.diagnosticsapp.auth.repositories.UserRepository;
import michal.malek.diagnosticsapp.auth.services.JWTService;
import michal.malek.diagnosticsapp.core.models.AppResponse;
import michal.malek.diagnosticsapp.core.models.ResponseType;
import michal.malek.diagnosticsapp.core.models.UserDiagnoseDTO;
import michal.malek.diagnosticsapp.core.models.UserEntity;
import michal.malek.diagnosticsapp.core.utills.JsonUtil;
import michal.malek.diagnosticsapp.diagnostics_part.mappers.DiagnoseMessageMapper;
import michal.malek.diagnosticsapp.diagnostics_part.models.DiagnoseEntity;
import michal.malek.diagnosticsapp.diagnostics_part.models.DiagnoseMessageEntity;
import michal.malek.diagnosticsapp.diagnostics_part.models.request.ChatModelType;
import michal.malek.diagnosticsapp.diagnostics_part.models.request.Message;
import michal.malek.diagnosticsapp.diagnostics_part.models.request.OpenAiRoles;
import michal.malek.diagnosticsapp.diagnostics_part.models.response.OpenAiResponse;
import michal.malek.diagnosticsapp.diagnostics_part.repositories.DiagnoseEntityRepository;
import michal.malek.diagnosticsapp.medic_data.models.UserData;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiagnoseService {

    private final OpenAiService openAiService;
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final PromptService promptService;
    private final DiagnoseEntityRepository diagnoseEntityRepository;

    //TODO REFACTOR
    @Transactional
    public String diagnose(String refreshToken, String medicalInterview, RedirectAttributes redirectAttributes, Long diagnoseEntityId, String chatType) throws JsonProcessingException {
        //System.out.println(chatType);
        ChatModelType chatModelType = ChatModelType.valueOf(chatType);

        if(Objects.equals(medicalInterview, "")){
            redirectAttributes.addFlashAttribute("message", new AppResponse("NO MEDICAL INTERVIEW", ResponseType.FAILURE));
            return "redirect:/diagnose";
        }

        String claimUserUid = jwtService.getClaimUserUid(refreshToken);
        UserEntity userEntity = userRepository.findByUid(claimUserUid);
        if(userEntity.getTokenAmount()< chatModelType.getTokenCost()){
            redirectAttributes.addFlashAttribute("message", new AppResponse("Lack of tokens, token are refilled every 7 days", ResponseType.FAILURE));
            return "redirect:/diagnose";
        }else {
            userEntity.setTokenAmount(userEntity.getTokenAmount()-chatModelType.getTokenCost());
            userRepository.saveAndFlush(userEntity);
        }

        UserData userData = userEntity.getUserData();
        DiagnoseEntity diagnoseEntity;
        List<Message> messages;
        if(diagnoseEntityId == null || diagnoseEntityId == 0){

            diagnoseEntity = new DiagnoseEntity();
            diagnoseEntity.setUserUid(claimUserUid);
            if(medicalInterview.length()>=30) {
                diagnoseEntity.setName(String.copyValueOf(medicalInterview.toCharArray(),0,29) + "...");
            }else{
                diagnoseEntity.setName("diagnose");
            }
            try {
                messages = promptService.promptConstructor(userData, medicalInterview);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("message", new AppResponse("Something went wrong (promptConstructor)", ResponseType.ERROR));
                return "redirect:/diagnose";
            }
        }else{

            Optional<DiagnoseEntity> byId = diagnoseEntityRepository.findById(diagnoseEntityId);
            if(byId.isPresent()){
                diagnoseEntity = byId.get();
                List<DiagnoseMessageEntity> diagnoseMessageEntityList = diagnoseEntity.getDiagnoseMessageEntityList();
                diagnoseMessageEntityList.add(new DiagnoseMessageEntity(medicalInterview,OpenAiRoles.user.toString()));
                diagnoseEntity.setDiagnoseMessageEntityList(diagnoseMessageEntityList);
                messages = DiagnoseMessageMapper.INSTANCE.toMessageList(diagnoseMessageEntityList);
            }else{
                redirectAttributes.addFlashAttribute("message", new AppResponse("Cannot find diagnose", ResponseType.ERROR));
                return "redirect:/diagnose";
            }

        }

        try {
            OpenAiResponse openAiResponse = openAiService.apiRequest(4000, chatModelType, messages);
            String json = JsonUtil.toJson(openAiResponse);
            System.out.println(json);
            OpenAiResponse openAiResponse1 = JsonUtil.fromJson(json, OpenAiResponse.class);
            String diagnose = openAiResponse1.getChoices().get(0).getMessage().getContent();

            List<DiagnoseMessageEntity> diagnoseMessageEntityList = diagnoseEntity.getDiagnoseMessageEntityList();
            diagnoseMessageEntityList.add(new DiagnoseMessageEntity(diagnose,OpenAiRoles.assistant.toString()));
            diagnoseEntity.setDiagnoseMessageEntityList(diagnoseMessageEntityList);
            diagnoseEntityRepository.save(diagnoseEntity);

            redirectAttributes.addFlashAttribute("diagnose", diagnoseEntity);
            return "redirect:/diagnose";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", new AppResponse("CHAT API FAILURE, please try later", ResponseType.ERROR));
            return "redirect:/diagnose";
        }

    }




    @Transactional
    public void refillTokens() {

        try {
            List<UserEntity> all = userRepository.findAll();
            for(UserEntity userEntity : all){
                UserType userType = userEntity.getUserType();
                userEntity.setTokenAmount(userType.getRefillTokenAmount());
            }
            userRepository.saveAllAndFlush(all);
        } catch (Exception e) {
            //TODO LOG
            throw new RuntimeException(e);
        }
    }

    public String listDiagnoses(String refreshToken, Model model) {
        String claimUserUid = jwtService.getClaimUserUid(refreshToken);
        List<DiagnoseEntity> diagnoses = diagnoseEntityRepository.findAllByUserUid(claimUserUid);
        model.addAttribute("diagnoses", diagnoses);
        return "diagnostics/chose-diagnose";
    }

    public String viewDiagnose(Long id, RedirectAttributes redirectAttributes) {
        Optional<DiagnoseEntity> diagnose = diagnoseEntityRepository.findById(id);
        if(diagnose.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", new AppResponse("NO SUCH DIAGNOSE", ResponseType.FAILURE));
            return "redirect:/diagnose";
        }
        redirectAttributes.addFlashAttribute("diagnose", diagnose.get());
        return "redirect:/diagnose";
    }

    public UserDiagnoseDTO diagnoseTemplate(String refreshToken) {
        String uid = jwtService.getClaimUserUid(refreshToken);
        UserEntity byUid = userRepository.findByUid(uid);
        return new UserDiagnoseDTO(byUid.getTokenAmount(), byUid.getEmail());
    }
}
