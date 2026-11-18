package com.taskflow.taskflow.service;

import com.taskflow.taskflow.dto.AuthResponse;
import com.taskflow.taskflow.dto.LoginRequest;
import com.taskflow.taskflow.dto.RegisterRequest;
import com.taskflow.taskflow.exception.ConflictException;
import com.taskflow.taskflow.exception.UnauthorizedException;
import com.taskflow.taskflow.model.User;
import com.taskflow.taskflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(userRepository, passwordEncoder, jwtEncoder, 60);
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        when(userRepository.existsByEmail("a@a.com")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> service.register(new RegisterRequest("a@a.com", "12345678")));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginShouldFailWithWrongPassword() {
        User user = new User();
        user.setId(1L);
        user.setEmail("a@a.com");
        user.setPasswordHash("hash");
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("errada", "hash")).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> service.login(new LoginRequest("a@a.com", "errada")));
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        User user = new User();
        user.setId(1L);
        user.setEmail("a@a.com");
        user.setPasswordHash("hash");
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("12345678", "hash")).thenReturn(true);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(
                Jwt.withTokenValue("token-falso").header("alg", "HS256").claim("sub", "1").build());

        AuthResponse response = service.login(new LoginRequest("A@a.com", "12345678"));

        assertEquals("token-falso", response.token());
    }
}
