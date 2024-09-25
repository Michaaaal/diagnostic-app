package michal.malek.diagnosticsapp.diagnostics_part.models.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class OpenAiRequest {
    private String model;
    private List<Message> messages;
    private int max_tokens;
    private final int n = 1;

}