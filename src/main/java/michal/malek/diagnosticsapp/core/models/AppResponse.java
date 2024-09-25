package michal.malek.diagnosticsapp.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppResponse {
    private String message;
    private ResponseType type;
}
