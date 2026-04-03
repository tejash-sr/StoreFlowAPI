$baseDir = "e:\GROOTAN\grootan-spring-boot-exercise-tejash\src\main\java\com\grootan\storeflow"
New-Item -ItemType Directory -Force -Path "$baseDir\security"
New-Item -ItemType Directory -Force -Path "$baseDir\config"

# DTOs
Set-Content -Path "$baseDir\dto\request\SignupRequestDto.java" -Value @"
package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
}
"@

Set-Content -Path "$baseDir\dto\request\AuthRequestDto.java" -Value @"
package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
"@

Set-Content -Path "$baseDir\dto\request\RefreshTokenRequestDto.java" -Value @"
package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDto {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
"@

Set-Content -Path "$baseDir\dto\request\ForgotPasswordRequestDto.java" -Value @"
package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
"@

Set-Content -Path "$baseDir\dto\request\ResetPasswordRequestDto.java" -Value @"
package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDto {
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;
}
"@

Set-Content -Path "$baseDir\dto\response\AuthResponseDto.java" -Value @"
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
"@

Set-Content -Path "$baseDir\dto\response\UserResponseDto.java" -Value @"
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
"@

# JWT Utilities
Set-Content -Path "$baseDir\security\JwtUtil.java" -Value @"
package com.grootan.storeflow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("`${app.jwt.secret:defaultSecretKeyWithAtLeast256BitsLength12345678901234567890}")
    private String secret;

    @Value("`${app.jwt.expiration-ms:3600000}") // 1 hour
    private long jwtExpirationMs;
    
    @Value("`${app.jwt.refresh-expiration-ms:86400000}") // 24 hours
    private long refreshExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        if (secret.length() < 32) {
            secret = "defaultSecretKeyWithAtLeast256BitsLength12345678901234567890";
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), jwtExpirationMs);
    }
    
    public String generateRefreshToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), refreshExpirationMs);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
"@
