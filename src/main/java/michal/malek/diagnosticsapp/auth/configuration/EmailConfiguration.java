package michal.malek.diagnosticsapp.auth.configuration;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Configuration
@RequiredArgsConstructor
public class EmailConfiguration {


    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String email;

    public void sendMail(String usersEmail, String content, String subject, boolean onCreate) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(email);
            helper.setTo(usersEmail);
            helper.setSubject(subject);
            helper.setText(content, true);

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            if (onCreate) {
                sendMail(usersEmail, content, subject, false);
            }
        }
    }


}
