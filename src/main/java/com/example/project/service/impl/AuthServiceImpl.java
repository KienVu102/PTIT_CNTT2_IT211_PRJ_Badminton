package com.example.project.service.impl;

import com.example.project.dto.request.LoginRequest;
import com.example.project.dto.request.RegisterRequest;
import com.example.project.dto.response.AuthResponse;
import com.example.project.entity.RefreshToken;
import com.example.project.entity.TokenBlacklist;
import com.example.project.entity.User;
import com.example.project.enums.Role;
import com.example.project.repository.RefreshTokenRepository;
import com.example.project.repository.TokenBlacklistRepository;
import com.example.project.repository.UserRepository;
import com.example.project.security.JwtUtil;
import com.example.project.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    // FR-04: Đăng ký tài khoản
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already exists");

        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already exists");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return buildAuthResponse(accessToken, refreshToken, user);
    }

    // FR-01: Đăng nhập — cấp phát JWT
    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        // Xác thực username + password qua Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        User user = (User) userDetailsService.loadUserByUsername(request.getUsername());

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // Xóa refresh token cũ (nếu có) rồi lưu token mới
        refreshTokenRepository.deleteByUserId(user.getId());
        saveRefreshToken(user, refreshToken);

        return buildAuthResponse(accessToken, refreshToken, user);
    }

    // FR-02: Xoay vòng Refresh Token
    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (stored.getExpiredAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Refresh token is expired, please login again");

        User user = stored.getUser();

        if (!user.isEnabled())
            throw new RuntimeException("Account is disabled");

        // Cấp access token mới, giữ nguyên refresh token
        String newAccessToken = jwtUtil.generateAccessToken(user);

        return buildAuthResponse(newAccessToken, refreshToken, user);
    }

    // FR-03: Đăng xuất — Blacklist access token
    @Override
    @Transactional
    public void logout(String accessToken) {

        // Tính thời điểm hết hạn còn lại của token
        LocalDateTime expiredAt = jwtUtil.extractExpiration(accessToken)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // Lưu token vào blacklist
        tokenBlacklistRepository.save(
                TokenBlacklist.builder()
                        .token(accessToken)
                        .expiredAt(expiredAt)
                        .build());

        // Xóa refresh token khỏi DB theo username
        String username = jwtUtil.extractUsername(accessToken);
        userRepository.findByUsername(username)
                .ifPresent(u -> refreshTokenRepository.deleteByUserId(u.getId()));
    }

    // Private helpers
    private void saveRefreshToken(User user, String rawToken) {
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .token(rawToken)
                        .user(user)
                        .expiredAt(LocalDateTime.now().plusDays(7))
                        .build());
    }

    private AuthResponse buildAuthResponse(String accessToken,
            String refreshToken,
            User user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}