package michal.malek.diagnosticsapp.medic_data.controllers;

import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.medic_data.services.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MedicDataController {

    private final AccountService accountService;

    @GetMapping("/add-drugs")
    public String addDrugs() {
        return "account/medic-data/add-drugs";
    }

    @GetMapping("/remove-drug")
    public String removeDrug(@RequestParam String medicId, @CookieValue(name = "refreshToken") String refreshToken ) {
        accountService.deleteDrug(medicId, refreshToken);
        return "account/medic-data/add-drugs";
    }

    @GetMapping("/add-drug")
    public String addDrug(@RequestParam String medicId, RedirectAttributes redirectAttributes , @CookieValue(name = "refreshToken") String refreshToken ){
        accountService.addDrugToUserData(medicId, redirectAttributes, refreshToken);
        return "account/medic-data/add-drugs";
    }
}
