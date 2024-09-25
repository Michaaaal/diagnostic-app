package michal.malek.diagnosticsapp.diagnostics_part.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class OpenAiRequest {
    private String model;
    private List<Message> messages;
    private int max_tokens;



    public String toJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}