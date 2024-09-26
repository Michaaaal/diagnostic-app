package michal.malek.diagnosticsapp.auth.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
public class GoogleOauthConfiguration {
   @Value("${Oauth2.secret}")
   private String clientSecret;

   @Value("${Oauth2.clientId}")
   private String clientId;

    @Bean
    public ClientRegistration googleClientRegistration() {


        return ClientRegistration.withRegistrationId("google")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientName("DiagnoseYourself")
                .redirectUri("http://localhost:8080/auth-callback")
                .scope("openid", "email", "profile")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://openidconnect.googleapis.com/v1/userinfo")
                .userNameAttributeName("sub")
                .build();
    }


    @Bean
    public String googleOAuth2redirectUrl() {
        String redirectUri = this.googleClientRegistration().getRedirectUri();
        String clientId = this.googleClientRegistration().getClientId();
        String scope = String.join(" ", this.googleClientRegistration().getScopes());

        String authorizationUri = this.googleClientRegistration().getProviderDetails().getAuthorizationUri();

        return String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&scope=%s&access_type=online",
                authorizationUri,
                clientId,
                redirectUri,
                scope);
    }
}
