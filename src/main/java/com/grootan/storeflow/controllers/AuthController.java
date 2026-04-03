package com.grootan.storeflow.controllers;

import com.grootan.storeflow.dto.request.*;
import com.grootan.storeflow.dto.response.AuthResponseDto;
import com.grootan.storeflow.dto.response.UserResponseDto;
import com.grootan.storeflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto signup(@Valid @RequestBody SignupRequestDto request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody AuthRequestDto request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponseDto refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        authService.forgotPassword(request);
    }

    @PostMapping("/reset-password/{token}")
    public void resetPassword(@PathVariable String token, @Valid @RequestBody ResetPasswordRequestDto request) {
        authService.resetPassword(token, request);
    }

    @GetMapping("/me")
    public UserResponseDto getCurrentUser() {
        return authService.getCurrentUser();
    }
}
