package michal.malek.diagnosticsapp.medic_data.controllers;

import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.medic_data.services.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller()
@RequestMapping("/account/admin")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AccountService accountService;

    @GetMapping("/update-drugs")
    public String updateDrugs(RedirectAttributes redirectAttributes) {
        return accountService.updateDrugs(redirectAttributes);
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "account/admin/dashboard";
    }

}
