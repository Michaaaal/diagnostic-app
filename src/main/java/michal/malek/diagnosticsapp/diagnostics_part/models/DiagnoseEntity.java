package michal.malek.diagnosticsapp.diagnostics_part.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class DiagnoseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String userUid;
    private String name;
    private Date date;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DiagnoseMessageEntity> diagnoseMessageEntityList;
    {
        diagnoseMessageEntityList = new ArrayList<>();
        date = new Date();
    }

}
