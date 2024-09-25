package michal.malek.diagnosticsapp.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class UserDiagnoseDTO {
    private int tokenAmount;
    private String email;
}
