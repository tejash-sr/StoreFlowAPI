package com.grootan.storeflow.service;

import com.grootan.storeflow.dto.request.*;
import com.grootan.storeflow.dto.response.AuthResponseDto;
import com.grootan.storeflow.dto.response.UserResponseDto;

public interface AuthService {
    AuthResponseDto signup(SignupRequestDto request);
    AuthResponseDto login(AuthRequestDto request);
    AuthResponseDto refreshToken(RefreshTokenRequestDto request);
    void forgotPassword(ForgotPasswordRequestDto request);
    void resetPassword(String token, ResetPasswordRequestDto request);
    UserResponseDto getCurrentUser();
    UserResponseDto uploadAvatar(org.springframework.web.multipart.MultipartFile file);
    org.springframework.core.io.Resource getAvatar();
}
