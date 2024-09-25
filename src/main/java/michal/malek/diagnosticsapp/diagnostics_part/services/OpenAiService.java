package michal.malek.diagnosticsapp.diagnostics_part.services;

import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.diagnostics_part.models.request.ChatModelType;
import michal.malek.diagnosticsapp.diagnostics_part.models.request.Message;
import michal.malek.diagnosticsapp.diagnostics_part.models.request.OpenAiRequest;
import michal.malek.diagnosticsapp.diagnostics_part.models.response.OpenAiResponse;
import michal.malek.diagnosticsapp.core.utills.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final RestTemplate restTemplate;
    @Value("${OpenAI.secret.key}")
    private final String apiSecretKey;


    public OpenAiResponse apiRequest(int tokenLimit, ChatModelType modelType, List<Message> messages) throws Exception {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiSecretKey);

        OpenAiRequest openAiRequest = new OpenAiRequest(modelType.toString(),messages,tokenLimit);
        String json = JsonUtil.toJson(openAiRequest);

        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        String strResponse = restTemplate.postForObject(url, entity, String.class);
        OpenAiResponse openAiResponse = JsonUtil.fromJson(strResponse, OpenAiResponse.class);
        return openAiResponse;
    }

}
