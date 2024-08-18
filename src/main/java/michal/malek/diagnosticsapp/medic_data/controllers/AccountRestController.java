package michal.malek.diagnosticsapp.medic_data.controllers;

import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.medic_data.services.AccountService;
import michal.malek.diagnosticsapp.medic_data.services.GoogleDriveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountService accountService;

    @GetMapping("/find-chronic-diseases")
    public ResponseEntity<?> findChronicDiseases(@RequestParam String str) {
        return ResponseEntity.ok(accountService.findChronicDiseases(str));
    }

    @GetMapping("/find-owned-chronic-diseases")
    public ResponseEntity<?> findOwnedChronicDiseases(@CookieValue(name="refreshToken") String refreshToken) {
        return ResponseEntity.ok(accountService.findOwnedChronicDiseases(refreshToken));
    }

    @GetMapping("/add-chronic-disease")
    public ResponseEntity<?> addChronicDisease(@RequestParam String name, @CookieValue(name = "refreshToken") String refreshToken) {
        return accountService.addChronicDiseaseToUserData(name, refreshToken);
    }

    @GetMapping("/remove-chronic-disease")
    public ResponseEntity<?> removeChronicDisease(@RequestParam String name, @CookieValue(name = "refreshToken") String refreshToken ) {
        return accountService.deleteChronicDisease(name, refreshToken);
    }

    @GetMapping("/find-drugs")
    public ResponseEntity<?> findDrugs(@RequestParam String str) {
        return ResponseEntity.ok(accountService.findDrugs(str));
    }

    @GetMapping("/find-owned-drugs")
    public ResponseEntity<?> findOwnedDrugs(@CookieValue(name="refreshToken") String refreshToken) {
        return ResponseEntity.ok(accountService.findOwnedDrugs(refreshToken));
    }

    @GetMapping("/add-drug")
    public ResponseEntity<?> addDrug(@RequestParam String medicId, @CookieValue(name = "refreshToken") String refreshToken ){
        return accountService.addDrugToUserData(medicId, refreshToken);
    }

    @GetMapping("/remove-drug")
    public ResponseEntity<?> removeDrug(@RequestParam String medicId, @CookieValue(name = "refreshToken") String refreshToken ) {
        return accountService.deleteDrug(medicId, refreshToken);
    }


    @GetMapping("find-owned-diagnostic-tests")
    public ResponseEntity<?> findOwnedDiagnosticTests(@CookieValue(name="refreshToken") String refreshToken){
        return ResponseEntity.ok(accountService.findOwnedDiagnosticTests(refreshToken));
    }

    @GetMapping("/remove-diagnostic-test")
    public ResponseEntity<?> deleteDiagnosticTest(@CookieValue(name="refreshToken") String refreshToken ,String driveId) {
        return accountService.removeDiagnosticTest(driveId, refreshToken);
    }



}
