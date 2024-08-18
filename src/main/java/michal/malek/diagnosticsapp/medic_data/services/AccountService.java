package michal.malek.diagnosticsapp.medic_data.services;

import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.auth.models.AuthResponse;
import michal.malek.diagnosticsapp.auth.models.ResponseType;
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
    private final DiagnosticTestService diagnosticTestService;
    private final JWTService jwtService;
    private final GoogleDriveService googleDriveService;

    public String updateChronicDiseases(RedirectAttributes redirectAttributes){
        try{
            chronicDiseaseService.updateChronicDiseases();
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("message", new AuthResponse("Something went wrong", ResponseType.ERROR));
            return "redirect:/account/admin/dashboard";
        }
        redirectAttributes.addFlashAttribute("message", new AuthResponse("diseases updated successfully", ResponseType.SUCCESS));
        return "redirect:/account/admin/dashboard";
    }

    public String updateDrugs(RedirectAttributes redirectAttributes){
        try{
            drugService.updateDrugs();
        }catch (IOException e){
            redirectAttributes.addFlashAttribute("message", new AuthResponse("Something went wrong", ResponseType.ERROR));
            return "redirect:/account/admin/dashboard";
        }
        redirectAttributes.addFlashAttribute("message", new AuthResponse("Drugs updated successfully", ResponseType.SUCCESS));
        return "redirect:/account/admin/dashboard";
    }

    public void listFilesOnGoogleDrive() throws IOException {
        List<File> files = googleDriveService.listFiles();
        files.forEach(elem -> System.out.println(elem.getName() + "  id:  " + elem.getId()));
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


    public String addDiagnosticTest(MultipartFile pdf, String type, RedirectAttributes redirectAttributes, String refreshToken) {
        String userUid = jwtService.getClaimUserUid(refreshToken);

        if(!"application/pdf".equals(pdf.getContentType()) && !" application/x-pdf".equals(pdf.getContentType())){
            redirectAttributes.addFlashAttribute("message", new AuthResponse("Wrong file format", ResponseType.FAILURE));
            return "redirect:/add-diagnostic-tests";
        }

        long maxSize = 80 * 1024;
        if (pdf.getSize() > maxSize) {
            redirectAttributes.addFlashAttribute("message", new AuthResponse("Too big file", ResponseType.FAILURE));
            return "redirect:/add-diagnostic-tests";
        }

        if(userDataService.addDiagnosticTest(userUid ,pdf, DiagnosticsTestsType.valueOf(type))){
            redirectAttributes.addFlashAttribute("message", new AuthResponse("Added diagnostic test", ResponseType.SUCCESS));
            return "redirect:/add-diagnostic-tests";
        }
        redirectAttributes.addFlashAttribute("message", new AuthResponse("Adding file failed", ResponseType.ERROR));
        return "redirect:/add-diagnostic-tests";
    }


    public ResponseEntity<?> removeDiagnosticTest(String driveId, String refreshToken){
        String userUid = jwtService.getClaimUserUid(refreshToken);
        try {
            googleDriveService.deleteFile(driveId);
            userDataService.deleteFile(driveId,userUid);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
           throw new RuntimeException("remove diagnostic test error");
        }
    }

    public ResponseEntity<?> findOwnedDiagnosticTests(String refreshToken) {
        String userUid = jwtService.getClaimUserUid(refreshToken);
        List<DiagnosticTest> diagnosticTests = userDataService.getOwnedDiagnosticTests(userUid);
        return ResponseEntity.ok().body(diagnosticTests);
    }


    public String deleteDriveFile(String driveId, RedirectAttributes redirectAttributes) {
        try {
            googleDriveService.deleteFile(driveId);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", new AuthResponse("Deleting file failed", ResponseType.FAILURE));
            return "redirect:/account/admin/dashboard";
        }
        redirectAttributes.addFlashAttribute("message", new AuthResponse("Deleting file succeed", ResponseType.SUCCESS));
        return "redirect:/account/admin/dashboard";
    }

    public String addPersonalData(PersonalDataDTO personalDataDTO, String refreshToken, RedirectAttributes redirectAttributes) {
        String userUid = jwtService.getClaimUserUid(refreshToken);
        try{
            userDataService.addPersonalData(personalDataDTO, userUid);
            redirectAttributes.addFlashAttribute("message", new AuthResponse("Adding Personal data success", ResponseType.SUCCESS));
            return "redirect:/add-personal-data";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("message", new AuthResponse("Adding data failed", ResponseType.ERROR));
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
