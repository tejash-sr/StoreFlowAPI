$baseDir = "e:\GROOTAN\grootan-spring-boot-exercise-tejash\src\test\java\com\grootan\storeflow\controllers"

Set-Content -Path "$baseDir\AuthControllerIntegrationTest.java" -Value @"
package com.grootan.storeflow.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grootan.storeflow.dto.request.AuthRequestDto;
import com.grootan.storeflow.dto.request.SignupRequestDto;
import com.grootan.storeflow.enums.Role;
import com.grootan.storeflow.models.User;
import com.grootan.storeflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFullName("Test User");
        user.setRole(Role.USER);
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Test
    void signup_success() throws Exception {
        SignupRequestDto request = new SignupRequestDto();
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setFullName("New User");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.user.email").value("new@example.com"));
    }

    @Test
    void login_success() throws Exception {
        AuthRequestDto request = new AuthRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()));
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        AuthRequestDto request = new AuthRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized()); // Because GlobalExceptionHandler maps AuthenticationFailedException to 401
    }
}
"@

$content = Get-Content -Path "$baseDir\ProductControllerIntegrationTest.java" -Raw
$content = $content -replace "public class ProductControllerIntegrationTest", "@org.springframework.security.test.context.support.WithMockUser(username=`"admin`", roles={`"ADMIN`"})`npublic class ProductControllerIntegrationTest"
Set-Content -Path "$baseDir\ProductControllerIntegrationTest.java" -Value $content

$content = Get-Content -Path "$baseDir\OrderControllerIntegrationTest.java" -Raw
$content = $content -replace "public class OrderControllerIntegrationTest", "@org.springframework.security.test.context.support.WithUserDetails(value=`"test@example.com`", setupBefore = org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION)`npublic class OrderControllerIntegrationTest"
Set-Content -Path "$baseDir\OrderControllerIntegrationTest.java" -Value $content

"@
