package michal.malek.diagnosticsapp.auth.models;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserRegisterDTO {
    @Email(message = "invalid email format")
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    @NotBlank(message = "2nd Password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password2;

    @AssertTrue(message = "Accept of policy is required")
    private boolean policyAccepted;
}
