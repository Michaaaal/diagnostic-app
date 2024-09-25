package michal.malek.diagnosticsapp.diagnostics_part.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import michal.malek.diagnosticsapp.diagnostics_part.models.response.OpenAiResponse;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DiagnoseMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
    private String role;
    {
        date = new Date();
    }

    public DiagnoseMessageEntity(String message, String role) {
        this.message = message;
        this.role = role;
    }
}
