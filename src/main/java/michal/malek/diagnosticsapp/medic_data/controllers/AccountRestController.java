package michal.malek.diagnosticsapp.medic_data.controllers;

import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.medic_data.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountService accountService;

    @GetMapping("/find-drugs")
    public ResponseEntity<?> findDrugs(@RequestParam String str) {
        return ResponseEntity.ok(accountService.findDrugs(str));
    }

    @GetMapping("/find-owned-drugs")
    public ResponseEntity<?> findOwnedDrugs(@CookieValue(name="refreshToken") String refreshToken) {
        return ResponseEntity.ok(accountService.findOwnedDrugs(refreshToken));
    }
}
