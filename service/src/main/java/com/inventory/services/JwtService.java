package com.inventory.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.ZoneOffset.UTC;

@Service
public class JwtService {
    private static final Logger logger
            = LoggerFactory.getLogger(JwtService.class);

    private static final String ISSUER = "com.inventory.bim";
    private static final String SECRET = "BlibliInventoryKey";

    public String generateToken(String email) throws RuntimeException {
        Date expiration = Date.from(LocalDateTime.now(UTC).plusHours(2).toInstant(UTC));
        String token = Jwts.builder()
                .setSubject(email)
                .setExpiration(expiration)
                .setIssuer(ISSUER)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        logger.info("token successfully generated!");
        return token;
    }

    public String verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return claims.getBody().getSubject();
        } catch (RuntimeException e) {
            logger.info("Parsing token failed. Token is invalid!");
            throw new RuntimeException("Failed parsing token!");
        }
    }
}
