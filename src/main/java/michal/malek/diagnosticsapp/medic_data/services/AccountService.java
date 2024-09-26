package michal.malek.diagnosticsapp.medic_data.services;

import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.core.models.AppResponse;
import michal.malek.diagnosticsapp.core.models.ResponseType;
import michal.malek.diagnosticsapp.auth.services.JWTService;
import michal.malek.diagnosticsapp.medic_data.models.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final DrugService drugService;
    private final UserDataService userDataService;
    private final ChronicDiseaseService chronicDiseaseService;
    private final JWTService jwtService;

    public String updateChronicDiseases(RedirectAttributes redirectAttributes){
        try{
            chronicDiseaseService.updateChronicDiseases();
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("message", new AppResponse("Something went wrong", ResponseType.ERROR));
            return "redirect:/account/admin/dashboard";
        }
        redirectAttributes.addFlashAttribute("message", new AppResponse("diseases updated successfully", ResponseType.SUCCESS));
        return "redirect:/account/admin/dashboard";
    }

    public String updateDrugs(RedirectAttributes redirectAttributes){
        try{
            drugService.updateDrugs();
        }catch (IOException e){
            redirectAttributes.addFlashAttribute("message", new AppResponse("Something went wrong", ResponseType.ERROR));
            return "redirect:/account/admin/dashboard";
        }
        redirectAttributes.addFlashAttribute("message", new AppResponse("Drugs updated successfully", ResponseType.SUCCESS));
        return "redirect:/account/admin/dashboard";
    }

    public Stream<Drug> findDrugs(String str) {
        return drugService.findDrugs(str);
    }

    public ResponseEntity<?> addDrugToUserData(String medicId,  String refreshToken) {
            String userUid = jwtService.getClaimUserUid(refreshToken);
            try{
                userDataService.addDrug(medicId, userUid);
            }catch (Exception e){
                throw new RuntimeException("addDrugToUserData err");
            }
        return ResponseEntity.ok().build();
    }

    public Set<Drug> findOwnedDrugs(String refreshToken) {
        String userUid = jwtService.getClaimUserUid(refreshToken);
        return userDataService.getOwnedDrugs(userUid);
    }

    public ResponseEntity<?> deleteDrug(String medicId , String refreshToken) {
        String userUid = jwtService.getClaimUserUid(refreshToken);
        try{
            userDataService.deleteDrug(medicId, userUid);
        }catch (Exception e){
            throw new RuntimeException("deleteDrug err");
        }
        return ResponseEntity.ok().build();
    }


    public Stream<ChronicDisease> findChronicDiseases(String str){
        return chronicDiseaseService.findChronicDisease(str);
    }

    public Set<ChronicDisease> findOwnedChronicDiseases(String refreshToken) {
        String userUid = jwtService.getClaimUserUid(refreshToken);
        return userDataService.getOwnedChronicDiseases(userUid);
    }

    public ResponseEntity<?> addChronicDiseaseToUserData(String name, String refreshToken) {
        String userUid = jwtService.getClaimUserUid(refreshToken);
            try{
                userDataService.addChronicDisease(name, userUid);
            }catch (Exception e){
                throw new RuntimeException("addChronicDiseaseToUserData err");
            }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deleteChronicDisease(String name , String refreshToken) {
        String userUid = jwtService.getClaimUserUid(refreshToken);
        try{
            userDataService.deleteChronicDisease(name, userUid);
        }catch (Exception e){
            throw new RuntimeException("delete chronic disease error");
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> findOwnedDiagnosticTests(String refreshToken) {
        String userUid = jwtService.getClaimUserUid(refreshToken);
        List<DiagnosticTest> diagnosticTests = userDataService.getOwnedDiagnosticTests(userUid);
        return ResponseEntity.ok().body(diagnosticTests);
    }


    public String addPersonalData(PersonalDataDTO personalDataDTO, String refreshToken, RedirectAttributes redirectAttributes) {
        String userUid = jwtService.getClaimUserUid(refreshToken);
        try{
            userDataService.addPersonalData(personalDataDTO, userUid);
            redirectAttributes.addFlashAttribute("message", new AppResponse("Adding Personal data success", ResponseType.SUCCESS));
            return "redirect:/add-personal-data";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("message", new AppResponse("Adding data failed", ResponseType.ERROR));
            return "redirect:/add-personal-data";
        }
    }

    public String displayAddPersonalData(Model model, String refreshToken) {
        String userUid = jwtService.getClaimUserUid(refreshToken);
        model.addAttribute("personalDataDTO", new PersonalDataDTO());
        PersonalDataDTO oldPersonalData = userDataService.getCurrentPersonalData(userUid);
        model.addAttribute("oldPersonalData",  oldPersonalData);
        return "account/medic-data/add-personal-data";
    }
}
