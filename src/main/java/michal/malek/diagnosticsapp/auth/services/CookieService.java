package michal.malek.diagnosticsapp.auth.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    public Cookie removeCookie(Cookie[] cookies, String name){
        for (Cookie cookie:cookies){
            if (cookie.getName().equals(name)){
                cookie.setPath("/");
                cookie.setMaxAge(0);
                cookie.setHttpOnly(true);
                return cookie;
            }
        }
        return null;
    }

    public void deleteTokenCookies(HttpServletRequest request, HttpServletResponse response){
        Cookie cookie = this.removeCookie(request.getCookies(),"token");
        if (cookie != null){
            response.addCookie(cookie);
        }
        cookie = this.removeCookie(request.getCookies(),"refresh");
        if (cookie != null){
            response.addCookie(cookie);
        }
        cookie = this.removeCookie(request.getCookies(),"JSESSIONID");
        if (cookie != null){
            response.addCookie(cookie);
        }
    }
}