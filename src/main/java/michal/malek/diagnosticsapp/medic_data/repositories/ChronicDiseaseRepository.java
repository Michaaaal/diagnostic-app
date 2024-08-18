package michal.malek.diagnosticsapp.medic_data.repositories;

import jakarta.transaction.Transactional;
import michal.malek.diagnosticsapp.medic_data.models.ChronicDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface ChronicDiseaseRepository extends JpaRepository<ChronicDisease,Long> {
    ChronicDisease findByName(String name);
    List<ChronicDisease> findAllByNameContainingIgnoreCase(String name);
}
