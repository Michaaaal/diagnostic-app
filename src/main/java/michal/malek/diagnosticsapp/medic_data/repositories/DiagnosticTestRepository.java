package michal.malek.diagnosticsapp.medic_data.repositories;

import michal.malek.diagnosticsapp.medic_data.models.DiagnosticTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosticTestRepository extends JpaRepository<DiagnosticTest, Long> {
    void deleteByDriveId(String driveId);
}
