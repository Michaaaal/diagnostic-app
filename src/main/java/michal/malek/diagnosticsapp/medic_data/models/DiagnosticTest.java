package michal.malek.diagnosticsapp.medic_data.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class DiagnosticTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private DiagnosticsTestsType testsType;
    private String fileUrl;
    private String driveId;

    public DiagnosticTest(DiagnosticsTestsType testsType, String fileUrl, String driveId) {
        this.testsType = testsType;
        this.fileUrl = fileUrl;
        this.driveId = driveId;
    }
}
