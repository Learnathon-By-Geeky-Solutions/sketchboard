package com.example.lostnfound.service.user;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

    private final String secretKey;

    public JWTService(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;  
    }

    
    public String generateToken(String email) {


        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                    .claims()
                    .add(claims)
                    .subject(email)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(Long.MAX_VALUE))
                    .and()
                    .signWith(getKey())
                    .compact();
        
    }

    private SecretKey getKey() {
       byte[] decodedKey = Decoders.BASE64.decode(secretKey);
       return Keys.hmacShaKeyFor(decodedKey);

    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
         return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
          
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String email=extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
