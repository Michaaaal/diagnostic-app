package michal.malek.diagnosticsapp.medic_data.repositories;

import michal.malek.diagnosticsapp.medic_data.models.Drug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrugRepository extends JpaRepository<Drug,Long> {
    Drug findByMedicId(String medicId);

    List<Drug> findAllByCommonNameContainingIgnoreCase(String name);
    List<Drug> findAllByProductNameContainingIgnoreCase(String name);
}
