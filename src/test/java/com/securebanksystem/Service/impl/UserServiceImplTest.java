package com.securebanksystem.Service.impl;

import com.securebanksystem.dto.UserDTO;
import com.securebanksystem.exception.DuplicateEmailException;
import com.securebanksystem.exception.UserNotFoundException;
import com.securebanksystem.model.User;
import com.securebanksystem.repository.UserRepository;
import com.securebanksystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    private User userEntity;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setEmail("test@gmail.com");
        userDTO.setPassword("rawPassword");

        userEntity = new User();
        userEntity.setId(1L);
        userEntity.setEmail("test@gmail.com");
    }

    @Test
    void testSaveUser_Success() {
        // 1. Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
                .thenReturn(null);

        // 2. Act - Call saveUser, NOT getAllUsers
        UserDTO result = userService.saveUser(userDTO);

        // 3. Assert - Use single object assertions
        assertNotNull(result);
        assertEquals("test@gmail.com", result.getEmail());

        // Verify that the database and external API were called
        verify(userRepository, times(1)).save(any(User.class));
        verify(restTemplate, times(1)).postForEntity(contains("/account"), any(), any());
    }

    @Test
    void testSaveUser_ThrowsDuplicateEmailException() {
        // Arrange
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> userService.saveUser(userDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // Act
        UserDTO result = userService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_ThrowsNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        doNothing().when(userRepository).delete(userEntity);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).delete(userEntity);
    }
    @Test
    void testUpdateById_Success() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("OldName");

        UserDTO updateInfo = new UserDTO();
        updateInfo.setFirstName("NewName");
        updateInfo.setLastName("Pawar");
        updateInfo.setEmail("new@test.com");
        // ... set other fields as needed

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        // When save is called, return the user (Mockito will return the object we tell it to)
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        // Act
        UserDTO result = userService.updateById(userId, updateInfo);

// Assert
        assertNotNull(result);
// Correct way: Access fields directly from the object
        assertEquals("NewName", result.getFirstName());
        assertEquals("Pawar", result.getLastName());
        assertEquals("new@test.com", result.getEmail());

        // Verify interactions
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateById_UserNotFound() {
        // Arrange
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateById(userId, new UserDTO()));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetAllUsers_Success() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Vishal");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Mahesh");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Vishal", result.get(0).getFirstName());
        assertEquals("Mahesh", result.get(1).getFirstName());

        verify(userRepository, times(1)).findAll();
    }
}