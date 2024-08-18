package michal.malek.diagnosticsapp.medic_data.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonalDataDTO {
    private String firstName;
    private String lastName;
    private int age;
    private int height;
    private double weight;
    private String gender;
}
