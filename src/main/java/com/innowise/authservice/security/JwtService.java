package com.innowise.authservice.security;

import com.innowise.authservice.entity.UserCredential;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final long ACCESS_TTL = 15 * 60 * 1_000L;
    private static final long REFRESH_TTL = 24 * 60 * 60 * 1_000L;

    private final SecretKey key;

    public JwtService(@Value("${spring.jwt.secret}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt.secret is required");
        }
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw  new IllegalStateException("secret must be at least 32 bytes for HS256");
        }
        key = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateAccessToken(UserCredential user) {
        return Jwts.builder()
                .setSubject(user.getLogin())
                .claim("userId", user.getUserId())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TTL))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserCredential user) {
        return Jwts.builder()
                .setSubject(user.getLogin())
                .claim("userId", user.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TTL))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
