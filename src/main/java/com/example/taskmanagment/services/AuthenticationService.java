package com.example.taskmanagment.services;

import com.example.taskmanagment.dto.response.JwtResponse;
import com.example.taskmanagment.dto.user.LoginDto;
import com.example.taskmanagment.exceptions.CantObtainFingerPrintException;
import com.example.taskmanagment.models.RefreshToken;
import com.example.taskmanagment.repositories.RefreshTokenRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.example.taskmanagment.utils.ResponseUtils.INVALID_FINGER_PRINT;
import static com.example.taskmanagment.utils.ResponseUtils.TOKEN_IS_COMPROMISED;
import static org.springframework.http.HttpHeaders.USER_AGENT;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {
    public static final String IP = "ip";

    private final EntityManager entityManager;

    private final JwTokenService tokenService;

    private final AuthenticationManager authManager;

    private final RefreshTokenRepository refreshTokenRepository;

    public JwtResponse login(LoginDto loginDto, HttpServletRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        logout(request, loginDto.getEmail());
        String accessToken = tokenService.generateAccessJws(userDetails.getUsername());
        return new JwtResponse(
                accessToken,
                persistNewRefreshToken(getFingerPrint(request), userDetails.getUsername()));
    }

    @Transactional(noRollbackFor = AccessDeniedException.class)
    public JwtResponse refresh(String refreshToken, HttpServletRequest request) {
        String fingerPrint = getFingerPrint(request);
        String tokenFingerPrint = tokenService.getFingerPrint(refreshToken);

        Optional<RefreshToken> maybeRefresh = refreshTokenRepository.findByToken(refreshToken);
        if (!Objects.equals(fingerPrint, tokenFingerPrint)
                || maybeRefresh.isEmpty()) {
            maybeRefresh.ifPresent(refreshTokenRepository::delete);
            logout(request, tokenService.getEmail(refreshToken));
            throw new AccessDeniedException(TOKEN_IS_COMPROMISED);
        }
        String newAccess = tokenService.generateAccessJws(tokenService.getEmail(refreshToken));
        maybeRefresh.ifPresent(refreshTokenRepository::delete);
        entityManager.flush();
        String newRefresh = persistNewRefreshToken(fingerPrint, tokenService.getEmail(refreshToken));
        return new JwtResponse(newAccess, newRefresh);
    }

    public void logout(HttpServletRequest request, String email) {
        tokenService.revokeTokenByFingerPrintAndEmail(getFingerPrint(request), email);
        entityManager.flush();
        SecurityContextHolder.clearContext();
    }


    private String persistNewRefreshToken(String fingerPrint, String email) {
        String newToken = tokenService.generateRefreshJws(fingerPrint, email);
        RefreshToken tokenEntity = RefreshToken.builder()
                .token(newToken)
                .fingerPrint(fingerPrint)
                .email(email)
                .build();
        refreshTokenRepository.save(tokenEntity);
        return newToken;
    }

    private String getFingerPrint(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT);
        String remoteAddr = request.getRemoteAddr();
        if (userAgent == null || remoteAddr == null || remoteAddr.isBlank() || userAgent.isBlank()) {
            throw new CantObtainFingerPrintException(INVALID_FINGER_PRINT);
        }

        Map<String, String> fingerPrint = new HashMap<>();
        fingerPrint.put(USER_AGENT, userAgent);
        fingerPrint.put(IP, remoteAddr);
        return fingerPrint.entrySet().stream()
                .map(stringStringEntry -> stringStringEntry.getKey() + ":" + stringStringEntry.getValue() + ";")
                .reduce(String::concat).orElseThrow(() -> new CantObtainFingerPrintException(INVALID_FINGER_PRINT));
    }
}
