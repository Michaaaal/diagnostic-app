package michal.malek.diagnosticsapp.auth.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthResponse {
    private String message;
    private ResponseType type;
}
