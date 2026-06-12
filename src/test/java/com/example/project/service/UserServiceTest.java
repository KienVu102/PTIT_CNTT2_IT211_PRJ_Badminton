package com.example.project.service;

import com.example.project.dto.response.UserResponse;
import com.example.project.entity.User;
import com.example.project.enums.Role;
import com.example.project.repository.UserRepository;
import com.example.project.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * FR-12: Unit test cho UserService (1 test method)
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@test.com")
                .password("encoded")
                .role(Role.ADMIN)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("getUserById - Lấy thông tin user theo ID thành công")
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.getUserById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getEmail()).isEqualTo("admin@test.com");
        assertThat(response.getRole()).isEqualTo(Role.ADMIN);
        assertThat(response.isEnabled()).isTrue();
        verify(userRepository).findById(1L);
    }
}
