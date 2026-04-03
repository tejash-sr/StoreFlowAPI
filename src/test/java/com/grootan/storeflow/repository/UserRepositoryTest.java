package com.grootan.storeflow.repository;

import com.grootan.storeflow.enums.Role;
import com.grootan.storeflow.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import com.grootan.storeflow.AbstractRepositoryTest;

public class UserRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveUser_savesCorrectly() {
        User user = User.builder()
                .email("test@example.com")
                .password("pwd")
                .fullName("Test User")
                .role(Role.USER)
                .build();
        User saved = userRepository.saveAndFlush(user);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertEquals("test@example.com", saved.getEmail());
    }

    @Test
    public void findByEmail_returnsUser() {
        User user = User.builder()
                .email("find@example.com")
                .password("pwd")
                .fullName("Find User")
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("find@example.com");
        assertTrue(found.isPresent());
        assertEquals("Find User", found.get().getFullName());
    }

    @Test
    public void existsByEmail_returnsTrueIfExists() {
        User user = User.builder()
                .email("exists@example.com")
                .password("pwd")
                .fullName("Exists User")
                .build();
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("exists@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    public void findByResetToken_returnsUser() {
        User user = User.builder()
                .email("token@example.com")
                .password("pwd")
                .fullName("Token User")
                .resetToken("my-token")
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByResetToken("my-token");
        assertTrue(found.isPresent());
        assertEquals("my-token", found.get().getResetToken());
    }
}
