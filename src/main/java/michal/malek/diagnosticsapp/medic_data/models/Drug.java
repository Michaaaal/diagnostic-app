package michal.malek.diagnosticsapp.medic_data.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Drug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String medicId;
    private String productName;
    private String commonName;
    private String leafletUrl;
    private String characteristicsUrl;

    public Drug(String medicId, String productName, String commonName, String leafletUrl, String characteristicsUrl) {
        this.medicId = medicId;
        this.productName = productName;
        this.commonName = commonName;
        this.leafletUrl = leafletUrl;
        this.characteristicsUrl = characteristicsUrl;
    }

}
