package com.grootan.storeflow.dto.response;

import lombok.Data;
import java.util.UUID;
import java.time.OffsetDateTime;

@Data
public class UserResponseDto {
    private UUID id;
    private String email;
    private String fullName;
    private String role;
    private String avatarPath;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
