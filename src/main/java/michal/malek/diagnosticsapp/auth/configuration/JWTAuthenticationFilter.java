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
            Cookie jwtCookie = cookieList.stream()
                    .filter(cookie -> "token".equals(cookie.getName()) || "refreshToken".equals(cookie.getName()))
                    .findFirst()
                    .orElse(null);

            if (jwtCookie != null) {
                try {

                    if(jwtCookie.getName().equals("token")) {
                        try{
                            jwtService.validateToken(jwtCookie.getValue());
                        }catch (ExpiredJwtException e){
                            jwtCookie = cookieList.stream()
                                    .filter(cookie -> "refreshToken".equals(cookie.getName()))
                                    .findFirst()
                                    .orElse(null);

                            if(jwtCookie != null) {
                                jwtService.validateToken(jwtCookie.getValue());
                            }else{
                                throw new Exception();
                            }
                        }
                    }
                    else{
                        jwtService.validateToken(jwtCookie.getValue());
                    }

                    String subject = jwtService.getSubject(jwtCookie.getValue());
                    String type = jwtService.getClaimUserType(jwtCookie.getValue());

                    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                    grantedAuthorities.add(UserType.STANDARD);
                    if(type.equals("PREMIUM")) {
                        grantedAuthorities.add(UserType.PREMIUM);
                    } else if (type.equals("ULTIMATE")) {
                        grantedAuthorities.add(UserType.PREMIUM);
                        grantedAuthorities.add(UserType.ULTIMATE);
                    } else if (type.equals("ADMIN")) {
                        grantedAuthorities.add(UserType.PREMIUM);
                        grantedAuthorities.add(UserType.ULTIMATE);
                        grantedAuthorities.add(UserType.ADMIN);
                    }


                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            subject, null, grantedAuthorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    if(jwtCookie.getName().equals("refreshToken")) {
                        String claimUserUid = jwtService.getClaimUserUid(jwtCookie.getValue());
                        response.addCookie( cookieService.generateCookie("token", jwtService.generateToken(claimUserUid ,type, subject ,jwtExp), jwtExp));
                        response.addCookie( cookieService.generateCookie("refreshToken", jwtService.generateToken(claimUserUid ,type, subject,jwtRefreshExp) , jwtRefreshExp));
                    }

                } catch (ExpiredJwtException e) {
                    System.out.println("Token expired, redirecting to login page");
                    response.sendRedirect(request.getContextPath() + "/logout");
                    return;
                } catch (Exception e) {
                    System.out.println("Something went wrong: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                    response.sendRedirect(request.getContextPath() + "/logout");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
