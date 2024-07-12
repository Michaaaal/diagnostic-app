package michal.malek.diagnosticsapp.auth;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import jakarta.servlet.http.Cookie;
import michal.malek.diagnosticsapp.auth.models.AuthResponse;
import michal.malek.diagnosticsapp.auth.models.ResponseType;
import michal.malek.diagnosticsapp.auth.repositories.UserRepository;
import michal.malek.diagnosticsapp.auth.services.AuthService;
import michal.malek.diagnosticsapp.auth.services.CookieService;
import michal.malek.diagnosticsapp.auth.services.JWTService;
import michal.malek.diagnosticsapp.models.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @Mock
    private CookieService cookieService;

    @InjectMocks
    private AuthService loginService;

    private MockHttpServletResponse response;
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    public void setUp() {
        response = new MockHttpServletResponse();
        redirectAttributes = new RedirectAttributesModelMap();
    }

    @Test
    public void testLoginSuccess() {
        String email = "test@example.com";
        String password = "password";
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword("encodedPassword");
        user.setUid("12345");

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(anyString(), "STANDARD",anyString(), anyInt())).thenReturn("jwtToken");
        when(cookieService.generateCookie(anyString(), anyString(), anyInt())).thenReturn(new Cookie("name", "value"));

        String result = loginService.login(email, password, response, redirectAttributes);

        assertEquals("redirect:/home", result);
        assertEquals(2, response.getCookies().length);
        assertEquals("Welcome Back", ((AuthResponse)redirectAttributes.getFlashAttributes().get("message")).getMessage());
        assertEquals(ResponseType.SUCCESS, ((AuthResponse)redirectAttributes.getFlashAttributes().get("message")).getType());
    }

    @Test
    public void testLoginUserNotFound() {
        String email = "test@example.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(null);

        String result = loginService.login(email, password, response, redirectAttributes);

        assertEquals("redirect:/login", result);
        assertEquals("Login Failed", ((AuthResponse)redirectAttributes.getFlashAttributes().get("message")).getMessage());
        assertEquals(ResponseType.FAILURE, ((AuthResponse)redirectAttributes.getFlashAttributes().get("message")).getType());
    }

    @Test
    public void testLoginInvalidPassword() {
        String email = "test@example.com";
        String password = "password";
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        String result = loginService.login(email, password, response, redirectAttributes);

        assertEquals("redirect:/login", result);
        assertEquals("Login Failed", ((AuthResponse)redirectAttributes.getFlashAttributes().get("message")).getMessage());
        assertEquals(ResponseType.FAILURE, ((AuthResponse)redirectAttributes.getFlashAttributes().get("message")).getType());
    }
}
