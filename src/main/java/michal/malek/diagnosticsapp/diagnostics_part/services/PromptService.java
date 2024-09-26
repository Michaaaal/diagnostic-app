package michal.malek.diagnosticsapp.diagnostics_part.services;

import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.auth.repositories.UserRepository;
import michal.malek.diagnosticsapp.auth.services.JWTService;
import michal.malek.diagnosticsapp.core.mappers.UserDataMapper;
import michal.malek.diagnosticsapp.core.utills.PdfReader;
import michal.malek.diagnosticsapp.diagnostics_part.models.prompts.PromptUserData;
import michal.malek.diagnosticsapp.diagnostics_part.models.prompts.PromptsConstantPart;
import michal.malek.diagnosticsapp.diagnostics_part.models.request.Message;
import michal.malek.diagnosticsapp.diagnostics_part.models.request.OpenAiRoles;
import michal.malek.diagnosticsapp.medic_data.models.DiagnosticTest;
import michal.malek.diagnosticsapp.medic_data.models.UserData;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptService {

    public List<Message> promptConstructor(UserData userData, String medicalInterview) throws IOException {
        PromptUserData promptUserData = UserDataMapper.INSTANCE.userDataToPromptUserData(userData);

        List<Message> messages = new ArrayList<>();
        messages.add(new Message(OpenAiRoles.system.toString(), PromptsConstantPart.START.toString()));
        messages.add(new Message(OpenAiRoles.user.toString(), promptUserData.toString() +" " +  getDiagnosticTestsAsString(promptUserData)));
        messages.add(new Message(OpenAiRoles.user.toString(), "MEDICAL INTERVIEW- " + medicalInterview));

        return messages;
    }

    private String getDiagnosticTestsAsString(PromptUserData promptUserData) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MEDICAL TESTS- ");

        List<DiagnosticTest> diagnosticTestList = promptUserData.getDiagnosticTestList();
        if(diagnosticTestList == null || diagnosticTestList.isEmpty()) {
            return "MEDICAL TESTS- lack of medical tests";
        }

        return stringBuilder.toString();
    }
}
