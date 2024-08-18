package michal.malek.diagnosticsapp.auth.configuration;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import michal.malek.diagnosticsapp.auth.models.UserType;
import michal.malek.diagnosticsapp.auth.services.CookieService;
import michal.malek.diagnosticsapp.auth.services.JWTService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final CookieService cookieService;
    private final List<String> openResources = List.of("/logout","/auth-callback","/reset-password-post","/reset-password","/retrieve-password-start","/logout","/account-activate","/login","/login-post","/register","/register-post", "/static/", "/favicon.ico","/retrieve-password");
    @Value("${jwt.exp}")
    private int jwtExp;
    @Value("${jwt.refresh.exp}")
    private int jwtRefreshExp;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(null);

        String path = request.getRequestURI();
        for (String openResource : openResources) {
            if(path.contains(openResource)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        System.out.println("JWT FILTER - URL: " + request.getRequestURL() + " Method: " + request.getMethod());

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {

            List<Cookie> cookieList = Arrays.asList(cookies);
            Cookie tokenCookie = null;
            Cookie refreshTokenCookie = null;

            for( var cookie : cookieList) {
                if(cookie.getName().equals("token")) {
                    tokenCookie = cookie;
                }
                if(cookie.getName().equals("refreshToken")) {
                    refreshTokenCookie = cookie;
                }
            }

            try{
                if(tokenCookie != null && refreshTokenCookie != null) {
                    try {
                        jwtService.validateToken(tokenCookie.getValue());
                    }catch (ExpiredJwtException e) {
                        jwtService.validateToken(refreshTokenCookie.getValue());

                        String subject = jwtService.getSubject(refreshTokenCookie.getValue());
                        String type = jwtService.getClaimUserType(refreshTokenCookie.getValue());
                        List<GrantedAuthority> grantedAuthorities = this.getType(type);
                        this.setAuthentication(subject,grantedAuthorities,request);

                        String claimUserUid = jwtService.getClaimUserUid(refreshTokenCookie.getValue());
                        response.addCookie( cookieService.generateCookie("token", jwtService.generateToken(claimUserUid ,type, subject ,jwtExp), jwtExp));
                        response.addCookie( cookieService.generateCookie("refreshToken", jwtService.generateToken(claimUserUid ,type, subject,jwtRefreshExp) , jwtRefreshExp));
                        filterChain.doFilter(request, response);
                    }

                    jwtService.validateToken(refreshTokenCookie.getValue());

                    String subject = jwtService.getSubject(refreshTokenCookie.getValue());
                    String type = jwtService.getClaimUserType(refreshTokenCookie.getValue());
                    List<GrantedAuthority> grantedAuthorities = this.getType(type);
                    this.setAuthentication(subject,grantedAuthorities,request);
                }
            }catch (Exception e){
                response.sendRedirect(request.getContextPath() + "/logout");
                return;
            }
        }else {
            response.sendRedirect(request.getContextPath() + "/logout");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private List<GrantedAuthority> getType(String type) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(UserType.STANDARD);
        switch (type) {
            case "PREMIUM" -> grantedAuthorities.add(UserType.PREMIUM);
            case "ULTIMATE" -> {
                grantedAuthorities.add(UserType.PREMIUM);
                grantedAuthorities.add(UserType.ULTIMATE);
            }
            case "ADMIN" -> {
                grantedAuthorities.add(UserType.PREMIUM);
                grantedAuthorities.add(UserType.ULTIMATE);
                grantedAuthorities.add(UserType.ADMIN);
            }
        }
        return grantedAuthorities;
    }

    private void setAuthentication(String subject, List<GrantedAuthority> grantedAuthorities, HttpServletRequest request){
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                subject, null, grantedAuthorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
