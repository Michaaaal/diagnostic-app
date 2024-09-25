package michal.malek.diagnosticsapp.diagnostics_part.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
        private Message message;
        private int index;
        private Object logprobs;
        @JsonProperty("finish_reason")
        private String finishReason;

        @NoArgsConstructor
        @Getter
        @Setter
        public static class Message {
            private String role;
            private String content;
            private String refusal;
        }
    }

    //TODO Remove in future
    @Override
    public String toString() {
        return "OpenAiResponse{" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", created=" + created +
                ", model='" + model + '\'' +
                ", choices=" + choices +
                '}';
    }
}
