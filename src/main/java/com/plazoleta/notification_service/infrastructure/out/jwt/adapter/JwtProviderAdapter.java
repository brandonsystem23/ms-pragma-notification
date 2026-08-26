package com.plazoleta.notification_service.infrastructure.out.jwt.adapter;


import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.spi.IJwtProviderPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtProviderAdapter implements IJwtProviderPort {

    private final String secret;
    private SecretKey secretKey;

    public JwtProviderAdapter(@Value("${security.jwt.secret}") String secret) {
        this.secret = secret;
    }

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public AuthSession validateAndGetUser(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return AuthSession.builder()
                .userId(claims.get("userId", Long.class))
                .fullName(claims.get("fullName", String.class))
                .role(claims.get("role", String.class))
                .numberDocument(claims.get("numberDocument", String.class))
                .phone(claims.get("phone", String.class))
                .email(claims.get("email", String.class))
                .build();
    }
}
