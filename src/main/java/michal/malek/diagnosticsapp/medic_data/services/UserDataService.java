package michal.malek.diagnosticsapp.medic_data.services;

import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.auth.exceptions.UserNotFound;
import michal.malek.diagnosticsapp.auth.services.JWTService;
import michal.malek.diagnosticsapp.core.models.UserEntity;
import michal.malek.diagnosticsapp.medic_data.models.Drug;
import michal.malek.diagnosticsapp.medic_data.models.UserData;
import michal.malek.diagnosticsapp.medic_data.repositories.DrugRepository;
import michal.malek.diagnosticsapp.medic_data.repositories.UserDataRepository;
import michal.malek.diagnosticsapp.auth.repositories.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class UserDataService {
    private final UserDataRepository userDataRepository;
    private final UserRepository userRepository;
    private final DrugRepository drugRepository;

    public void addDrug(String medicId, String userUid) {
        UserEntity byUid = userRepository.findByUid(userUid);
        if(byUid!=null){
            try{
                UserData userData = byUid.getUserData();
                Drug byMedicId = drugRepository.findByMedicId(medicId);
                userData.getDrugList().add(byMedicId);
                userDataRepository.saveAndFlush(userData);
            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException();
            }
        }else throw new UserNotFound();

    }

    public List<Drug> getOwnedDrugs(String userUid) {
        UserEntity byUid = userRepository.findByUid(userUid);
        UserData userData = byUid.getUserData();
        return userData.getDrugList();
    }

    @Transactional
    public void deleteDrug(String medicId, String userUid) {
        UserEntity byUid = userRepository.findByUid(userUid);
        UserData userData = byUid.getUserData();
        userData.getDrugList().removeIf(elem -> elem.getMedicId().equals(medicId));
        userDataRepository.saveAndFlush(userData);
    }
}
