package michal.malek.diagnosticsapp.auth.repositories;

import michal.malek.diagnosticsapp.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByUid(String uid);
    void deleteAllByEnabledFalse();

}
