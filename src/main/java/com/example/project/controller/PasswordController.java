package com.example.project.controller;

import com.example.project.dto.request.ChangePasswordRequest;
import com.example.project.dto.request.ForgotPasswordRequest;
import com.example.project.dto.response.ApiResponse;
import com.example.project.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// FR-10
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    // FR-10a: Đổi mật khẩu (phải đăng nhập)
    @PatchMapping("/auth/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        passwordService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    // FR-10b: Quên mật khẩu (public)
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        passwordService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
    }
}