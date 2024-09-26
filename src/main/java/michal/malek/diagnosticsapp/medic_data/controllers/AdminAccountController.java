package michal.malek.diagnosticsapp.medic_data.controllers;

import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.core.models.AppResponse;
import michal.malek.diagnosticsapp.core.models.ResponseType;
import michal.malek.diagnosticsapp.medic_data.services.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller()
@RequestMapping("/account/admin")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AccountService accountService;

    @GetMapping("/update-drugs")
    public String updateDrugs(RedirectAttributes redirectAttributes) {
        return accountService.updateDrugs(redirectAttributes);
    }

    @GetMapping("/update-chronic-diseases")
    public String updateChronicDiseases(RedirectAttributes redirectAttributes) {
        return accountService.updateChronicDiseases(redirectAttributes);
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "account/admin/dashboard";
    }


}
