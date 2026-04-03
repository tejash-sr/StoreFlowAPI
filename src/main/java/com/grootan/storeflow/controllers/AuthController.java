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

    @PostMapping(value = "/me/avatar", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponseDto uploadAvatar(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        return authService.uploadAvatar(file);
    }

    @GetMapping("/me/avatar")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> getAvatar() {
        org.springframework.core.io.Resource resource = authService.getAvatar();
        String contentType = "image/jpeg";
        return org.springframework.http.ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
