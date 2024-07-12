package michal.malek.diagnosticsapp.auth.repositories;

import michal.malek.diagnosticsapp.auth.models.RetrievePasswordOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetrievePasswordOperationRepository extends JpaRepository<RetrievePasswordOperation, Long> {
    List<RetrievePasswordOperation> findByUserUid(String userUid);
    void deleteAllByUserUid(String userUid);
    RetrievePasswordOperation findByUid(String uid);

}
