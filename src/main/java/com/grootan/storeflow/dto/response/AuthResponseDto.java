package com.grootan.storeflow.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
    private UserResponseDto user;
}
