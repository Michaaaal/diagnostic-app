package michal.malek.diagnosticsapp.medic_data.models;

import jakarta.persistence.*;
import lombok.Data;
import michal.malek.diagnosticsapp.core.models.UserEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    protected int age;
    protected double weight;
    protected int height;
    protected String gender;
    @ManyToMany
    protected Set<ChronicDisease> chronicDiseaseSet;
    @ManyToMany
    protected Set<Drug> drugSet;
    @OneToMany(cascade = CascadeType.ALL)
    protected List<DiagnosticTest> diagnosticTestList;
    {
        chronicDiseaseSet = new HashSet<>();
        drugSet = new HashSet<>();
        diagnosticTestList = new ArrayList<>();
    }

}
