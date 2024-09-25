package michal.malek.diagnosticsapp.diagnostics_part.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.auth.services.JWTService;
import michal.malek.diagnosticsapp.core.models.AppResponse;
import michal.malek.diagnosticsapp.core.models.ResponseType;
import michal.malek.diagnosticsapp.core.models.UserDiagnoseDTO;
import michal.malek.diagnosticsapp.diagnostics_part.models.DiagnoseEntity;
import michal.malek.diagnosticsapp.diagnostics_part.repositories.DiagnoseEntityRepository;
import michal.malek.diagnosticsapp.diagnostics_part.services.DiagnoseService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DiagnosticsController {

    private final DiagnoseService diagnoseService;

    private final DiagnoseEntityRepository diagnoseEntityRepository;
    private final JWTService jwtService;

    @GetMapping("/diagnose-yourself")
    public String diagnose(@CookieValue(name = "refreshToken") String refreshToken,
                       @RequestParam("medicalInterview") String medicalInterview,
                       @RequestParam("chatType") String chatType,
                       @RequestParam(value = "diagnoseEntityId", required = false) Long diagnoseEntityId,
                       RedirectAttributes redirectAttributes) {

        try {
            return diagnoseService.diagnose(refreshToken, medicalInterview, redirectAttributes, diagnoseEntityId, chatType);
        } catch (JsonProcessingException e) {
        //TODO
            return "redirect:/login";
        }
    }

    @GetMapping("/diagnose")
    public String diagnoseTemplate(@CookieValue("refreshToken") String refreshToken,@ModelAttribute("diagnose") DiagnoseEntity diagnoseEntity, Model model) {
        UserDiagnoseDTO userDiagnoseDTO = diagnoseService.diagnoseTemplate(refreshToken);
        model.addAttribute("diagnose", diagnoseEntity);
        model.addAttribute("userDiagnoseDTO", userDiagnoseDTO);
        model.addAttribute("medicalInterview", "");
        return "diagnostics/diagnose";
    }



    @GetMapping("/chose-diagnose")
    public String listDiagnoses(Model model, @CookieValue(name = "refreshToken") String refreshToken) {
        return diagnoseService.listDiagnoses(refreshToken, model);
    }

    @GetMapping("/chose-diagnose/{id}")
    public String viewDiagnose(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        return diagnoseService.viewDiagnose(id,redirectAttributes);
    }



    @Scheduled(cron = "0 0 5 ? * MON")
    public void refillTokens(){
        diagnoseService.refillTokens();
    }

}
