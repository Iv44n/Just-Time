package com.justtimeapi.api.services;

import com.justtimeapi.api.dto.request.LoginRequest;
import com.justtimeapi.api.dto.response.AuthResponse;
import com.justtimeapi.api.enums.Token;
import com.justtimeapi.api.exception.exceptions.InvalidRefreshTokenException;
import com.justtimeapi.api.exception.exceptions.RoleNotFoundException;
import com.justtimeapi.api.exception.exceptions.SessionNotFoundException;
import com.justtimeapi.api.exception.exceptions.UserNotFoundException;
import com.justtimeapi.api.models.AppSession;
import com.justtimeapi.api.models.AppUser;

import com.justtimeapi.api.repository.RoleRepository;
import com.justtimeapi.api.repository.SessionRepository;
import com.justtimeapi.api.repository.UserRepository;
import com.justtimeapi.api.repository.UserRoleRepository;
import com.justtimeapi.api.services.token.JwtService;
import com.justtimeapi.api.utils.UserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.justtimeapi.api.dto.request.RegisterRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse loginUser(LoginRequest request) {
        AppUser user = userRepository.findUserByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        AppSession session = sessionRepository.createSession(user.getId());

        String accessToken = jwtService.generateToken(Token.ACCESS_TOKEN, session.getId(), user.getId());
        String refreshToken = jwtService.generateToken(Token.REFRESH_TOKEN, session.getId(), null);

        return new AuthResponse(
                accessToken,
                refreshToken,
                UserAdapter.toResponse(user));
    }

    @Transactional
    public AuthResponse createAccount(RegisterRequest request) {
        String pwdHashed = passwordEncoder.encode(request.password());

        AppUser u = AppUser.builder()
                .username(request.username())
                .email(request.email())
                .password(pwdHashed)
                .build();

        AppUser savedUser = userRepository.createUser(u);

        UUID roleId = roleRepository.findRoleIdByName(request.role().name())
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + request.role().name()));

        userRoleRepository.assignRoleToUser(savedUser.getId(), roleId);

        AppSession session = sessionRepository.createSession(savedUser.getId());

        String accessToken = jwtService.generateToken(Token.ACCESS_TOKEN, session.getId(), savedUser.getId());
        String refreshToken = jwtService.generateToken(Token.REFRESH_TOKEN, session.getId(), null);

        return new AuthResponse(
                accessToken,
                refreshToken,
                UserAdapter.toResponse(savedUser));
    }

    public String refreshUserAccessToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        UUID sessionId = jwtService.extractSessionId(refreshToken);

        AppSession session = sessionRepository.findSessionById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found or expired"));

        AppUser user = userRepository.findUserById(session.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return jwtService.generateToken(Token.ACCESS_TOKEN, session.getId(), user.getId());
    }

    public void logout(String accessToken) {
        if (jwtService.isTokenValid(accessToken) || jwtService.isAccessToken(accessToken)) {
            UUID sessionId = jwtService.extractSessionId(accessToken);

            if (sessionId == null) {
                throw new SessionNotFoundException("Session not found for the provided token");
            }

            sessionRepository.deleteSessionById(sessionId);
        }
    }

    public AppUser getCurrentAppUserByCookie(String cookie) {
        UUID userId = jwtService.extractUserId(cookie);
        return userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
