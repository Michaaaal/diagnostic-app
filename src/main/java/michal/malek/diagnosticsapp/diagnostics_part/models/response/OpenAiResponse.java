package michal.malek.diagnosticsapp.diagnostics_part.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OpenAiResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Choice {
        private String text;
        private int index;
        private Object logprobs;
        private String finishReason;

    }
}
