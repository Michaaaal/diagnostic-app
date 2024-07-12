package michal.malek.diagnosticsapp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ChronicDisease {
    @Id
    private Long id;
}
