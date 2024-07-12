package michal.malek.diagnosticsapp.auth.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDTO {
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    @NotBlank(message = "2nd Password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password2;

    private String operationUid;

    public ResetPasswordDTO(String operationUid) {
        this.operationUid = operationUid;
    }
}
