package michal.malek.diagnosticsapp.auth.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String SECRET;

    public void validateToken(final String token) throws ExpiredJwtException, IllegalArgumentException {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String userUid, String userType, String username, int exp){
        Map<String, Object> claims = new HashMap<>();
        claims.put("userUid", userUid);
        claims.put("type", userType.toString());
        return createToken(claims,username,exp);
    }
    public String createToken(Map<String,Object> claims, String username,int exp){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+exp))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    public String getSubject(final String token){
        return Jwts
                .parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getClaimUserUid(final String token){
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();

            return claims.get("userUid").toString();
        } catch (Exception e) {
            throw new RuntimeException("Unable to extract user UID from token", e);
        }
    }

    public String getClaimUserType(final String token){
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();

            return claims.get("type").toString();
        } catch (Exception e) {
            throw new RuntimeException("Unable to extract user Type from token", e);
        }
    }


}
