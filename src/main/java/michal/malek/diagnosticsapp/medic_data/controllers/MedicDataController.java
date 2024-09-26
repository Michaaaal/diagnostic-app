package michal.malek.diagnosticsapp.medic_data.controllers;

import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.medic_data.models.PersonalDataDTO;
import michal.malek.diagnosticsapp.medic_data.services.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MedicDataController {

    private final AccountService accountService;

    @GetMapping("/add-chronic-diseases")
    public String addChronicDiseases() {
        return "account/medic-data/add-chronic-diseases";
    }

    @GetMapping("/medic-data")
    public String medicData() {
        return "account/medic-data";
    }

    @GetMapping("/add-drugs")
    public String addDrugs() {
        return "account/medic-data/add-drugs";
    }

    @GetMapping("add-diagnostic-tests")
    public String addDiagnosticTest(){
        return "account/medic-data/add-diagnostic-tests";
    }

    @GetMapping("add-personal-data")
    public String displayAddPersonalData(Model model, @CookieValue(name="refreshToken") String refreshToken){
        return accountService.displayAddPersonalData(model, refreshToken);
    }

    @PostMapping("/submit-personal-info")
    public String submitPersonalInfo(@ModelAttribute PersonalDataDTO personalDataDTO, @CookieValue(name = "refreshToken") String refreshToken, RedirectAttributes redirectAttributes) {
        return accountService.addPersonalData(personalDataDTO, refreshToken, redirectAttributes);
    }

    @GetMapping("/upgrade-account")
    public String upgradeAccount(){
        return "account/upgrade-account";
    }

    @GetMapping("/info")
    public String info(){
        return "/others/info";
    }
}
