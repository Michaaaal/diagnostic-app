package michal.malek.diagnosticsapp.medic_data.repositories;

import michal.malek.diagnosticsapp.core.models.UserEntity;
import michal.malek.diagnosticsapp.medic_data.models.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataRepository extends JpaRepository<UserData, Long> {

}
