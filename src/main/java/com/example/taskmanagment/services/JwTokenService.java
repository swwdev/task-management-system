package com.example.taskmanagment.services;


import com.example.taskmanagment.repositories.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

import static com.example.taskmanagment.utils.ResponseUtils.INVALID_REFRESH_TOKEN;

@Service
public class JwTokenService {
    private static final String FINGER_PRINT = "finger_print";
    private final SecretKey key;
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.access-lifetime}")
    private Duration accessLifeTime;
    @Value("${jwt.refresh-lifetime}")
    private Duration refreshLifeTime;

    public JwTokenService(@Value("${jwt.secret-key}") String secret, RefreshTokenRepository refreshTokenRepository) {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateAccessJws(String userName) {
        Date expirationDate = new Date(accessLifeTime.toMillis() + System.currentTimeMillis());
        return Jwts.builder()
                .subject(userName)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    public String getEmail(String token) {
        return getTokenPayload(token).getSubject();
    }

    public String generateRefreshJws(String fingerPrint, String email) {
        Date expirationDate = new Date(System.currentTimeMillis() + refreshLifeTime.toMillis());
        return Jwts.builder()
                .subject(email)
                .expiration(expirationDate)
                .claims()
                .add(FINGER_PRINT, fingerPrint)
                .and()
                .signWith(key)
                .compact();
    }


    public String getFingerPrint(String refreshToken) {
        Claims tokenPayload = getTokenPayload(refreshToken);
        String fingerPrint = (String) tokenPayload.get(FINGER_PRINT);
        if (fingerPrint == null)
            throw new AccessDeniedException(INVALID_REFRESH_TOKEN);
        return fingerPrint;
    }


    @Transactional
    public void revokeTokenByFingerPrintAndEmail(String fingerPrint, String email) {
        refreshTokenRepository.findByFingerPrintAndEmail(fingerPrint, email)
                .ifPresent(refreshTokenRepository::delete);
    }

    private Claims getTokenPayload(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
