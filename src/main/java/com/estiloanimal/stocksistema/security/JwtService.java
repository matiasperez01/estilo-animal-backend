package com.estiloanimal.stocksistema.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Service
public class JwtService {

    private static final Duration EXPIRATION = Duration.ofHours(12);

    private final SecretKey key;

    public JwtService(@Value("${app.jwt-secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(String subject) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + EXPIRATION.toMillis());
        return Jwts.builder()
                .subject(subject)
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(key)
                .compact();
    }

    public String validarYObtenerSubject(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
