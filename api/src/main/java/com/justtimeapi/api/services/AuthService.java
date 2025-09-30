package com.justtimeapi.api.services;

import com.justtimeapi.api.dto.request.LoginRequest;
import com.justtimeapi.api.dto.response.AuthResponse;
import com.justtimeapi.api.models.AppSession;
import com.justtimeapi.api.models.AppUser;

import com.justtimeapi.api.models.Token;
import com.justtimeapi.api.repository.SessionRepository;
import com.justtimeapi.api.repository.UserRepository;
import com.justtimeapi.api.services.token.JwtService;
import com.justtimeapi.api.utils.UserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.justtimeapi.api.dto.request.RegisterRequest;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse loginUser(LoginRequest request) {
        Optional<AppUser> userOpt = userRepository.findUserByEmail(request.email());

        if (userOpt.isEmpty()) {
            throw new BadCredentialsException("Invalid credentials");
        }

        AppUser user = userOpt.get();

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        AppSession session = sessionRepository.createSession(user.getId());

        String accessToken = jwtService.generateToken(Token.ACCESS_TOKEN, session.getId(), user.getId());
        String refreshToken = jwtService.generateToken(Token.REFRESH_TOKEN, session.getId(), null);

        return new AuthResponse(
                accessToken,
                refreshToken,
                UserAdapter.toResponse(user));
    }

    public AuthResponse createAccount(RegisterRequest request) {
        String pwdHashed = passwordEncoder.encode(request.password());

        AppUser u = new AppUser(null, request.username(), request.email(), pwdHashed, null, null);
        AppUser savedUser = userRepository.createUser(u);

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
            throw new BadCredentialsException("Invalid refresh token");
        }

        UUID sessionId = jwtService.extractSessionId(refreshToken);

        Optional<AppSession> sessionOpt = sessionRepository.findSessionById(sessionId);

        if (sessionOpt.isEmpty()) {
            throw new BadCredentialsException("Session not found or expired");
        }

        AppSession session = sessionOpt.get();

        Optional<AppUser> userOpt = userRepository.findUserById(session.getUserId());

        if (userOpt.isEmpty()) {
            throw new BadCredentialsException("User not found");
        }

        AppUser user = userOpt.get();

        return jwtService.generateToken(Token.ACCESS_TOKEN, session.getId(), user.getId());
    }

    public void logout(String accessToken) {
        if (jwtService.isTokenValid(accessToken) || jwtService.isAccessToken(accessToken)) {
            UUID sessionId = jwtService.extractSessionId(accessToken);

            if (sessionId != null) {
                sessionRepository.deleteSessionById(sessionId);
            }
        }
    }

    public Optional<AppUser> getCurrentAppUser(String email) {
        return userRepository.findUserByEmail(email);
    }
}
