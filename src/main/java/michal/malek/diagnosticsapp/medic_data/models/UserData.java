package michal.malek.diagnosticsapp.medic_data.models;

import jakarta.persistence.*;
import lombok.Data;
import michal.malek.diagnosticsapp.core.models.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private double weight;
    private String gender;
    @ManyToMany
    private List<ChronicDisease> chronicDiseaseList;
    @ManyToMany
    private List<Drug> drugList;
    @OneToMany
    private List<DiagnosticTest> diagnosticTestList;
    {
        chronicDiseaseList = new ArrayList<>();
        drugList = new ArrayList<>();
        diagnosticTestList = new ArrayList<>();
    }


}
