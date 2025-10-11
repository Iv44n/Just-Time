package com.justtimeapi.api.security;

import com.justtimeapi.api.models.AppUser;
import com.justtimeapi.api.repository.SessionRepository;
import com.justtimeapi.api.repository.UserRepository;
import com.justtimeapi.api.repository.UserRoleRepository;
import com.justtimeapi.api.services.token.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final UserRoleRepository userRoleRepository;

    public static String getJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        Cookie[] cookies = request.getCookies();

        boolean existsInHeader = (authHeader != null && authHeader.startsWith("Bearer "));
        boolean existsInCookies = (cookies != null &&
                Arrays.stream(cookies)
                        .anyMatch(cookie -> cookie.getName().equals("accessToken")));

        if (existsInHeader) {
            return authHeader.substring(7);
        } else if (existsInCookies) {
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("accessToken"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtService.isTokenValid(jwt) && jwtService.isAccessToken(jwt)) {
                UUID userId = jwtService.extractUserId(jwt);
                UUID sessionId = jwtService.extractSessionId(jwt);

                if (userId != null && sessionId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    var userOpt = userRepository.findUserById(userId);
                    var sessionOpt = sessionRepository.findSessionById(sessionId);

                    if (userOpt.isPresent() && sessionOpt.isPresent()) {
                        AppUser user = userOpt.get();

                        var roles = userRoleRepository.findRolesByUserId(user.getId());

                        var authorities = roles.stream()
                                .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                                .toList();

                        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(user.getEmail())
                                .password(user.getPassword())
                                .authorities(authorities)
                                .build();

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            throw new SecurityException(e);
        }

        filterChain.doFilter(request, response);
    }
}
