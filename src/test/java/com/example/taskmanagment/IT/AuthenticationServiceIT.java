package com.example.taskmanagment.IT;


import com.example.taskmanagment.dto.response.JwtResponse;
import com.example.taskmanagment.dto.user.LoginDto;
import com.example.taskmanagment.models.RefreshToken;
import com.example.taskmanagment.repositories.RefreshTokenRepository;
import com.example.taskmanagment.services.AuthenticationService;
import com.example.taskmanagment.services.JwTokenService;
import com.example.taskmanagment.util.IntegrationTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.taskmanagment.util.test_data.TokenDataUtil.FINGER_PRINT1;
import static com.example.taskmanagment.util.test_data.UserDataUtil.EMAIL;
import static com.example.taskmanagment.util.test_data.UserDataUtil.createLoginDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.USER_AGENT;

@Transactional
public class AuthenticationServiceIT extends IntegrationTestBase {

    private final LoginDto loginDto = createLoginDto();
    private MockHttpServletRequest request;
    @Autowired
    private RefreshTokenRepository tokenRepository;
    @Autowired
    private JwTokenService jwTokenService;
    @Autowired
    private AuthenticationService authenticationService;
    private RefreshToken savedRefresh;

    @BeforeEach
    void setTokens() {
        RefreshToken tokenEntity1 = RefreshToken.builder()
                .token(jwTokenService.generateRefreshJws(FINGER_PRINT1, EMAIL))
                .fingerPrint(FINGER_PRINT1)
                .email(EMAIL)
                .build();
        savedRefresh = tokenRepository.save(tokenEntity1);
    }

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.setRemoteAddr("0::1");
        request.addHeader(USER_AGENT, "browser1");
    }

    @Test
    void login() {
        JwtResponse jwtResponse = authenticationService.login(loginDto, request);
        assertAll(
                () -> {
                    Optional<RefreshToken> maybeToken = tokenRepository.findByToken(jwtResponse.getRefreshToken());
                    assertThat(maybeToken).isPresent();
                    assertThat(maybeToken.get().getId()).isNotEqualTo(savedRefresh.getId());
                },
                () -> assertThat(jwtResponse.getAccessToken()).isNotBlank())
        ;
    }

    @Test
    void refresh() {
        JwtResponse jwtResponse = authenticationService.refresh(savedRefresh.getToken(), request);
        assertAll(
                () -> assertThat(jwtResponse.getRefreshToken()).isNotBlank(),
                () -> assertThat(jwtResponse.getAccessToken()).isNotBlank())
        ;
    }

    @Test
    void logout() {
        authenticationService.logout(request, EMAIL);
        Assertions.assertThat(tokenRepository.findByFingerPrintAndEmail(FINGER_PRINT1, EMAIL)).isNotPresent();
    }

}
