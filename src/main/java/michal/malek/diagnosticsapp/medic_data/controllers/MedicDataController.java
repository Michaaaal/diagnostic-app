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

    @GetMapping("/add-drugs")
    public String addDrugs() {
        return "account/medic-data/add-drugs";
    }

    @GetMapping("add-diagnostic-tests")
    public String addDiagnosticTest(){
        return "account/medic-data/add-diagnostic-tests";
    }

    @PostMapping("add-diagnostic-test")
    public String addDiagnosticTest(@RequestParam("file") MultipartFile multipartFile, @RequestParam String diagnosticTestType , RedirectAttributes redirectAttributes, @CookieValue String refreshToken) {
        return accountService.addDiagnosticTest(multipartFile, diagnosticTestType,  redirectAttributes, refreshToken);
    }

    @GetMapping("add-personal-data")
    public String displayAddPersonalData(Model model, @CookieValue(name="refreshToken") String refreshToken){
        return accountService.displayAddPersonalData(model, refreshToken);
    }

    @PostMapping("/submit-personal-info")
    public String submitPersonalInfo(@ModelAttribute PersonalDataDTO personalDataDTO, @CookieValue(name = "refreshToken") String refreshToken, RedirectAttributes redirectAttributes) {
        return accountService.addPersonalData(personalDataDTO, refreshToken, redirectAttributes);
    }
}
