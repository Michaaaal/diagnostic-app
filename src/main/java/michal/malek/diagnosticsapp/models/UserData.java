package michal.malek.diagnosticsapp.models;

import jakarta.persistence.*;
import lombok.Data;

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
    @OneToMany
    private List<ChronicDisease> chronicDiseaseList;
    @OneToMany
    private List<Drug> drugs;



}
