package com.inventory.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Date;

import static java.time.ZoneOffset.UTC;

@Component
public class JwtService {

    private static final String ISSUER = "com.inventory.bim";
    private static final String SECRET = "BlibliInventoryKey";

    public String generateToken(String email) throws IOException, URISyntaxException {
        Date expiration = Date.from(LocalDateTime.now(UTC).plusHours(2).toInstant(UTC));
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(expiration)
                .setIssuer(ISSUER)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public String verifyToken(String token) throws IOException, URISyntaxException {
        Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
        //returning authenticated/verified username
        return claims.getBody().getSubject();
    }

    public boolean validateToken(String token, UserDetails user) {
        try {
            if (this.verifyToken(token).equals(user.getUsername()))
                return true;
        } catch (IOException | URISyntaxException e) {
            return false;
        }
        return false;
    }
}
