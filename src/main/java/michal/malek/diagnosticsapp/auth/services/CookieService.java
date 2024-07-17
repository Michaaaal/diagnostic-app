package michal.malek.diagnosticsapp.auth.services;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    public Cookie generateCookie(String name, String value, int exp){
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        cookie.setMaxAge(exp);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        return cookie;
    }


}