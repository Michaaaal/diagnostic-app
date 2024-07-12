package michal.malek.diagnosticsapp.auth.services;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.RequiredArgsConstructor;

import lombok.Setter;
import michal.malek.diagnosticsapp.auth.configuration.EmailConfiguration;
import michal.malek.diagnosticsapp.models.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Setter
public class EmailService {

    private final EmailConfiguration emailConfiguration;

    @Value("${front.url}")
    private String frontUrl;

    @Value("classpath:/templates/email/activate.html")
    Resource activeTemplate;

    @Value("classpath:/templates/email/retrieve-password-email.html")
    Resource retrieveTemplate;

    public void sendActivation(UserEntity user) throws IOException {
        try{
            String html = Files.toString(activeTemplate.getFile() , Charsets.UTF_8);
            String newUrl = frontUrl + "/account-activate?uid=" + user.getUid();
            html = html.replace("https://google.com",newUrl);
            emailConfiguration.sendMail(user.getEmail(), html,"DiagnoseYourself Account Activation",true);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void sendPasswordRecovery(UserEntity user, String operationUid) throws IOException {
        try{
            String html = Files.toString(retrieveTemplate.getFile() , Charsets.UTF_8);
            String newUrl = frontUrl + "/reset-password?operationUid=" + operationUid;
            html = html.replace("https://google.com",newUrl);
            emailConfiguration.sendMail(user.getEmail(),html,"DiagnoseYourself Retrieve Password",true);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}

