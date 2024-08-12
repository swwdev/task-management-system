package com.example.taskmanagment.unit;

import com.example.taskmanagment.dto.response.JwtResponse;
import com.example.taskmanagment.dto.user.CustomUserDetails;
import com.example.taskmanagment.dto.user.LoginDto;
import com.example.taskmanagment.exceptions.CantObtainFingerPrintException;
import com.example.taskmanagment.models.RefreshToken;
import com.example.taskmanagment.models.User;
import com.example.taskmanagment.repositories.RefreshTokenRepository;
import com.example.taskmanagment.services.AuthenticationService;
import com.example.taskmanagment.services.JwTokenService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Optional;

import static com.example.taskmanagment.util.test_data.TokenDataUtil.ACCESS;
import static com.example.taskmanagment.util.test_data.TokenDataUtil.FINGER_PRINT1;
import static com.example.taskmanagment.util.test_data.TokenDataUtil.FINGER_PRINT2;
import static com.example.taskmanagment.util.test_data.TokenDataUtil.REFRESH;
import static com.example.taskmanagment.util.test_data.UserDataUtil.EMAIL;
import static com.example.taskmanagment.util.test_data.UserDataUtil.PASSWORD;
import static com.example.taskmanagment.util.test_data.UserDataUtil.createLoginDto;
import static com.example.taskmanagment.util.test_data.UserDataUtil.createUser;
import static com.example.taskmanagment.utils.ResponseUtils.TOKEN_IS_COMPROMISED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mock.Strictness.LENIENT;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.USER_AGENT;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    private final User user = createUser();
    private final LoginDto loginDto = createLoginDto();
    @Mock
    private JwTokenService tokenService;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock(strictness = LENIENT)
    private EntityManager entityManager;
    @InjectMocks
    private AuthenticationService authenticationService;
    private MockHttpServletRequest request1;
    private MockHttpServletRequest request2;

    @BeforeEach
    void setUp() {
        request1 = new MockHttpServletRequest();
        request1.setRemoteAddr("0::1");
        request1.addHeader(USER_AGENT, "browser1");

        request2 = new MockHttpServletRequest();
        request2.setRemoteAddr("0::2");
        request2.addHeader(USER_AGENT, "browser2");

        doNothing().when(entityManager).flush();
    }

    @Test
    void login() {
        doReturn(new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(user),
                null,
                new ArrayList<>()))
                .when(authManager).authenticate(new UsernamePasswordAuthenticationToken(EMAIL, PASSWORD));
        doReturn(ACCESS).when(tokenService).generateAccessJws(EMAIL);
        doReturn(REFRESH).when(tokenService).generateRefreshJws(FINGER_PRINT1, EMAIL);

        assertThat(authenticationService.login(loginDto, request1))
                .isEqualTo(new JwtResponse(ACCESS, REFRESH));
        verify(tokenService).revokeTokenByFingerPrintAndEmail(FINGER_PRINT1, EMAIL);
    }


    @Test
    void refreshIfTokenAlreadyRevoked() {
        doReturn(Optional.empty()).when(refreshTokenRepository).findByToken(REFRESH);
        doReturn(FINGER_PRINT1).when(tokenService).getFingerPrint(REFRESH);
        doReturn(EMAIL).when(tokenService).getEmail(REFRESH);

        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> authenticationService.refresh(REFRESH, request1),
                TOKEN_IS_COMPROMISED);
        verify(tokenService).revokeTokenByFingerPrintAndEmail(anyString(), anyString());
    }

    @Test
    void refreshIfFingerPrintDiffer() {
        doReturn(Optional.of(new RefreshToken())).when(refreshTokenRepository).findByToken(REFRESH);
        doReturn(FINGER_PRINT2).when(tokenService).getFingerPrint(REFRESH);
        doReturn(EMAIL).when(tokenService).getEmail(REFRESH);

        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> authenticationService.refresh(REFRESH, request1),
                TOKEN_IS_COMPROMISED);
        verify(refreshTokenRepository).delete(any());
    }


    @Test
    void successRefresh() {

        doReturn(Optional.of(new RefreshToken())).when(refreshTokenRepository).findByToken(REFRESH);
        doReturn(FINGER_PRINT1).when(tokenService).getFingerPrint(REFRESH);
        doReturn(EMAIL).when(tokenService).getEmail(REFRESH);
        doReturn(ACCESS).when(tokenService).generateAccessJws(EMAIL);
        doReturn(REFRESH).when(tokenService).generateRefreshJws(FINGER_PRINT1, EMAIL);

        assertThat(authenticationService.refresh(REFRESH, request1))
                .isEqualTo(new JwtResponse(ACCESS, REFRESH));
        verify(refreshTokenRepository).delete(any());
    }

    @Test
    void logout() {
        doNothing().when(tokenService).revokeTokenByFingerPrintAndEmail(FINGER_PRINT1, EMAIL);

        assertThatNoException().isThrownBy(() -> authenticationService.logout(request1, EMAIL));
        verify(tokenService).revokeTokenByFingerPrintAndEmail(FINGER_PRINT1, EMAIL);
        verify(entityManager).flush();
    }

    @Test
    void persistNewRefreshToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        doReturn(REFRESH).when(tokenService).generateRefreshJws(FINGER_PRINT1, EMAIL);
        doReturn(new RefreshToken()).when(refreshTokenRepository).save(any());

        Method persistNewRefreshToken = AuthenticationService.class.getDeclaredMethod("persistNewRefreshToken", String.class, String.class);
        persistNewRefreshToken.setAccessible(true);

        assertThat(persistNewRefreshToken.invoke(authenticationService, FINGER_PRINT1, EMAIL))
                .isEqualTo(REFRESH);
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void getFingerPrint() throws NoSuchMethodException {
        Method getFingerPrint = AuthenticationService.class.getDeclaredMethod("getFingerPrint", HttpServletRequest.class);
        getFingerPrint.setAccessible(true);

        Assertions.assertAll(
                () -> assertThat(getFingerPrint.invoke(authenticationService, request1))
                        .isEqualTo(FINGER_PRINT1),
                () -> assertThat(getFingerPrint.invoke(authenticationService, request2))
                        .isEqualTo(FINGER_PRINT2)
        );
    }

    @Test
    void getFingerPrintIfFingerPrintIsInvalid() throws NoSuchMethodException {
        Method getFingerPrint = AuthenticationService.class.getDeclaredMethod("getFingerPrint", HttpServletRequest.class);
        getFingerPrint.setAccessible(true);

        request1.setRemoteAddr(null);
        request2.removeHeader(USER_AGENT);
        Assertions.assertAll(
                () -> assertThatThrownBy(() -> {
                    try {
                        getFingerPrint.invoke(authenticationService, request1);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                }).isInstanceOf(CantObtainFingerPrintException.class),
                () -> assertThatThrownBy(() -> {
                    try {
                        getFingerPrint.invoke(authenticationService, request2);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                }).isInstanceOf(CantObtainFingerPrintException.class)
        );
    }
}
