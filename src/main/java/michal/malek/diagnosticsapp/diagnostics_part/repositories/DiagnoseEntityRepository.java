package michal.malek.diagnosticsapp.diagnostics_part.repositories;

import michal.malek.diagnosticsapp.diagnostics_part.models.DiagnoseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnoseEntityRepository extends JpaRepository<DiagnoseEntity, Long> {

    List<DiagnoseEntity> findAllByUserUid(String userUid);
}
