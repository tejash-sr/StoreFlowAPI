package com.grootan.storeflow.service.impl;

import com.grootan.storeflow.dto.request.*;
import com.grootan.storeflow.dto.response.AuthResponseDto;
import com.grootan.storeflow.dto.response.UserResponseDto;
import com.grootan.storeflow.enums.Role;
import com.grootan.storeflow.exceptions.AppException;
import com.grootan.storeflow.exceptions.AuthenticationFailedException;
import com.grootan.storeflow.mapper.UserMapper;
import com.grootan.storeflow.models.User;
import com.grootan.storeflow.repository.UserRepository;
import com.grootan.storeflow.security.CustomUserDetails;
import com.grootan.storeflow.security.JwtUtil;
import com.grootan.storeflow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public AuthResponseDto signup(SignupRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already in use", HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(Role.USER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        UserDetails userDetails = new CustomUserDetails(savedUser);

        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toDto(savedUser))
                .build();
    }

    @Override
    public AuthResponseDto login(AuthRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        UserDetails userDetails = new CustomUserDetails(user);

        return AuthResponseDto.builder()
                .accessToken(jwtUtil.generateToken(userDetails))
                .refreshToken(jwtUtil.generateRefreshToken(userDetails))
                .user(userMapper.toDto(user))
                .build();
    }

    @Override
    public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
        String token = request.getRefreshToken();
        String userEmail = jwtUtil.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (jwtUtil.validateToken(token, userDetails)) {
            return AuthResponseDto.builder()
                    .accessToken(jwtUtil.generateToken(userDetails))
                    .refreshToken(token)
                    .user(userMapper.toDto(((CustomUserDetails)userDetails).getUser()))
                    .build();
        }
        throw new AuthenticationFailedException("Invalid refresh token");
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiresAt(OffsetDateTime.now().plusHours(1));
            userRepository.save(user);
            
            // In a real app we send an email here. Stub logic goes here:
            // emailService.sendPasswordResetEmail(user.getEmail(), token);
        }
    }

    @Override
    @Transactional
    public void resetPassword(String token, ResetPasswordRequestDto request) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new AppException("Invalid token", HttpStatus.BAD_REQUEST));
        
        if (user.getResetTokenExpiresAt() == null || user.getResetTokenExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new AppException("Expired token", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiresAt(null);
        userRepository.save(user);
    }

    @Override
    public UserResponseDto getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return userMapper.toDto(((CustomUserDetails) auth.getPrincipal()).getUser());
        }
        throw new AuthenticationFailedException("User not authenticated");
    }
}
