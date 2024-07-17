package michal.malek.diagnosticsapp.medic_data.services;

import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.auth.exceptions.UserNotFound;
import michal.malek.diagnosticsapp.medic_data.models.Drug;
import michal.malek.diagnosticsapp.auth.models.AuthResponse;
import michal.malek.diagnosticsapp.auth.models.ResponseType;
import michal.malek.diagnosticsapp.auth.services.JWTService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final DrugService drugService;
    private final UserDataService userDataService;
    private final JWTService jwtService;

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

    public Stream<Drug> findDrugs(String str) {
        return drugService.findDrugs(str);
    }

    public String addDrugToUserData(String medicId, RedirectAttributes redirectAttributes, String refreshToken) {
        try{
            String userUid = jwtService.getClaimUserUid(refreshToken);
            try{
                userDataService.addDrug(medicId, userUid);
            }catch (Exception e){
                redirectAttributes.addFlashAttribute("message", new AuthResponse("Something went wrong", ResponseType.ERROR));
                return "redirect:/add-drugs";
            }
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("message", new AuthResponse("Something wrong with Token", ResponseType.ERROR));
            return "redirect:/add-drugs";
        }
        redirectAttributes.addFlashAttribute("message", new AuthResponse("Drug added", ResponseType.SUCCESS));
        return "redirect:/add-drugs";
    }

    public List<Drug> findOwnedDrugs(String refreshToken) {
        String userUid = jwtService.getClaimUserUid(refreshToken);
        return userDataService.getOwnedDrugs(userUid);
    }

    public void deleteDrug(String medicId , String refreshToken) {
        try{
            String userUid = jwtService.getClaimUserUid(refreshToken);
            userDataService.deleteDrug(medicId, userUid);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
