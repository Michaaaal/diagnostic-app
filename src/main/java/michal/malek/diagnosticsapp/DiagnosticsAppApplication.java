package michal.malek.diagnosticsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
public class DiagnosticsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiagnosticsAppApplication.class, args);
    }

}
