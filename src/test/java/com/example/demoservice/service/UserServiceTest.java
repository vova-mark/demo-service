package com.example.demoservice.service;

import com.example.demoservice.dto.User;
import com.example.demoservice.dto.UserCreateRequest;
import com.example.demoservice.dto.UserUpdateRequest;
import com.example.demoservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUser_shouldSaveAndReturnUser() {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("testuser");
        req.setPassword("password");
        req.setEmail("test@example.com");
        req.setFirstName("Test");
        req.setLastName("User");
        req.setEnabled(true);
        req.setRole("USER");
        User user = new User();
        user.setId(1L);
        user.setUsername(req.getUsername());
        when(userRepository.save(any(User.class))).thenReturn(user);
        User result = userService.saveUser(req);
        assertEquals("testuser", result.getUsername());
        assertEquals(1L, result.getId());
    }

    @Test
    void findUserById_shouldReturnUserIfExistsAndNotDeleted() {
        User user = new User();
        user.setId(1L);
        user.setDeleted(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findUserById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findUserById_shouldReturnEmptyIfDeleted() {
        User user = new User();
        user.setId(1L);
        user.setDeleted(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findUserById(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteUserById_shouldSoftDeleteUser() {
        User user = new User();
        user.setId(1L);
        user.setDeleted(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        boolean deleted = userService.deleteUserById(1L);
        assertTrue(deleted);
        verify(userRepository).save(user);
        assertTrue(user.isDeleted());
    }

    @Test
    void deleteUserById_shouldReturnFalseIfNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        boolean deleted = userService.deleteUserById(1L);
        assertFalse(deleted);
    }

    @Test
    void findAllUsers_shouldFilterDeletedUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setDeleted(false);
        User user2 = new User();
        user2.setId(2L);
        user2.setDeleted(true);
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<User> users = userService.findAllUsers();
        assertEquals(1, users.size());
        assertEquals(1L, users.get(0).getId());
    }

    @Test
    void updateUser_shouldUpdateUserIfExistsAndNotDeleted() {
        User user = new User();
        user.setId(1L);
        user.setDeleted(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserUpdateRequest req = new UserUpdateRequest();
        req.setUsername("updated");
        req.setEmail("updated@example.com");
        req.setFirstName("Updated");
        req.setLastName("User");
        req.setEnabled(true);
        req.setRole("USER");
        Optional<User> result = userService.updateUser(1L, req);
        assertTrue(result.isPresent());
        assertEquals("updated", result.get().getUsername());
    }

    @Test
    void updateUser_shouldReturnEmptyIfDeletedOrNotFound() {
        User user = new User();
        user.setId(1L);
        user.setDeleted(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserUpdateRequest req = new UserUpdateRequest();
        Optional<User> result = userService.updateUser(1L, req);
        assertTrue(result.isEmpty());
    }
}

