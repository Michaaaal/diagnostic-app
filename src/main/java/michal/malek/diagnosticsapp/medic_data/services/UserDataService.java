package michal.malek.diagnosticsapp.medic_data.services;

import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.auth.exceptions.UserNotFound;
import michal.malek.diagnosticsapp.auth.services.JWTService;
import michal.malek.diagnosticsapp.core.mappers.UserDataMapper;
import michal.malek.diagnosticsapp.core.models.UserEntity;
import michal.malek.diagnosticsapp.medic_data.models.*;
import michal.malek.diagnosticsapp.medic_data.repositories.ChronicDiseaseRepository;
import michal.malek.diagnosticsapp.medic_data.repositories.DiagnosticTestRepository;
import michal.malek.diagnosticsapp.medic_data.repositories.DrugRepository;
import michal.malek.diagnosticsapp.medic_data.repositories.UserDataRepository;
import michal.malek.diagnosticsapp.auth.repositories.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class UserDataService {
    private final UserDataRepository userDataRepository;
    private final UserRepository userRepository;
    private final DrugRepository drugRepository;
    private final ChronicDiseaseRepository chronicDiseaseRepository;
    private final DiagnosticTestRepository diagnosticTestRepository;


    public void addDrug(String medicId, String userUid) {
        UserEntity byUid = userRepository.findByUid(userUid);
        if(byUid!=null){
                UserData userData = byUid.getUserData();
                Drug byMedicId = drugRepository.findByMedicId(medicId);
                userData.getDrugSet().add(byMedicId);
                userDataRepository.saveAndFlush(userData);
        }else throw new UserNotFound();
    }

    public Set<Drug> getOwnedDrugs(String userUid) {
        UserEntity byUid = userRepository.findByUid(userUid);
        UserData userData = byUid.getUserData();
        return userData.getDrugSet();
    }

    @Transactional
    public void deleteDrug(String medicId, String userUid) {
        UserEntity byUid = userRepository.findByUid(userUid);
        UserData userData = byUid.getUserData();
        userData.getDrugSet().removeIf(elem -> elem.getMedicId().equals(medicId));
        userDataRepository.saveAndFlush(userData);
    }

    public void addChronicDisease(String name, String userUid) {
        UserEntity byUid = userRepository.findByUid(userUid);
        if(byUid!=null){
            UserData userData = byUid.getUserData();
            ChronicDisease byName = chronicDiseaseRepository.findByName(name);
            userData.getChronicDiseaseSet().add(byName);
            userDataRepository.saveAndFlush(userData);
        }else throw new UserNotFound();
    }

    @Transactional
    public void deleteChronicDisease(String name, String userUid) {
        UserEntity byUid = userRepository.findByUid(userUid);
        UserData userData = byUid.getUserData();
        userData.getChronicDiseaseSet().removeIf(elem -> elem.getName().equals(name));
        userDataRepository.saveAndFlush(userData);
    }

    public Set<ChronicDisease> getOwnedChronicDiseases(String userUid) {
        UserEntity byUid = userRepository.findByUid(userUid);
        return byUid.getUserData().getChronicDiseaseSet();
    }

    public List<DiagnosticTest> getOwnedDiagnosticTests(String userUid){
        UserEntity byUid = userRepository.findByUid(userUid);
        UserData userData = byUid.getUserData();
        return userData.getDiagnosticTestList();
    }


    @Transactional
    public void deleteFile(String driveId, String userUid) {
        UserEntity byUid = userRepository.findByUid(userUid);
        UserData userData = byUid.getUserData();
        userData.getDiagnosticTestList().removeIf(elem -> elem.getDriveId().equals(driveId));
        userDataRepository.saveAndFlush(userData);
        diagnosticTestRepository.deleteByDriveId(driveId);
    }

    public PersonalDataDTO getCurrentPersonalData(String userUid) {
        UserEntity byUid = userRepository.findByUid(userUid);
        UserData userData = byUid.getUserData();
        if(userData.getAge() == 0 || userData.getGender() == null || userData.getWeight() == 0 || userData.getHeight() == 0
                || userData.getFirstName().isEmpty() || userData.getLastName().isEmpty()){
            return null;
        }
        return UserDataMapper.INSTANCE.userDataToPersonalDataDTO(userData);
    }

    public void addPersonalData(PersonalDataDTO personalDataDTO, String userUid) {
        UserEntity byUid = userRepository.findByUid(userUid);
        UserData userData = byUid.getUserData();
        userData.setFirstName(personalDataDTO.getFirstName());
        userData.setLastName(personalDataDTO.getLastName());
        userData.setGender(personalDataDTO.getGender());
        userData.setAge(personalDataDTO.getAge());
        userData.setWeight(personalDataDTO.getWeight());
        userData.setHeight(personalDataDTO.getHeight());
        userDataRepository.saveAndFlush(userData);
    }
}
