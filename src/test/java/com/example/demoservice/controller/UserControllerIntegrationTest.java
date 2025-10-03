package com.example.demoservice.controller;

import com.example.demoservice.dto.UserCreateRequest;
import com.example.demoservice.dto.UserUpdateRequest;
import com.example.demoservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_andGetUserById() throws Exception {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("integrationuser");
        req.setPassword("password123");
        req.setEmail("integration@example.com");
        req.setFirstName("Integration");
        req.setLastName("User");
        req.setEnabled(true);
        req.setRole("USER");

        ResultActions createResult = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        createResult.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("integrationuser"));

        Long userId = objectMapper.readTree(createResult.andReturn().getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integrationuser"));
    }

    @Test
    void updateUser_shouldUpdateUser() throws Exception {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("toUpdate");
        req.setPassword("password123");
        req.setEmail("update@example.com");
        req.setFirstName("To");
        req.setLastName("Update");
        req.setEnabled(true);
        req.setRole("USER");
        Long userId = objectMapper.readTree(mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString()).get("id").asLong();

        UserUpdateRequest updateReq = new UserUpdateRequest();
        updateReq.setUsername("updated");
        updateReq.setEmail("updated@example.com");
        updateReq.setFirstName("Updated");
        updateReq.setLastName("User");
        updateReq.setEnabled(true);
        updateReq.setRole("USER");

        mockMvc.perform(put("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated"));
    }

    @Test
    void deleteUser_shouldSoftDeleteUser() throws Exception {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("todelete");
        req.setPassword("password123");
        req.setEmail("delete@example.com");
        req.setFirstName("To");
        req.setLastName("Delete");
        req.setEnabled(true);
        req.setRole("USER");
        Long userId = objectMapper.readTree(mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_shouldListNonDeletedUsers() throws Exception {
        UserCreateRequest req1 = new UserCreateRequest();
        req1.setUsername("user1");
        req1.setPassword("password1");
        req1.setEmail("user1@example.com");
        req1.setFirstName("User");
        req1.setLastName("One");
        req1.setEnabled(true);
        req1.setRole("USER");
        UserCreateRequest req2 = new UserCreateRequest();
        req2.setUsername("user2");
        req2.setPassword("password2");
        req2.setEmail("user2@example.com");
        req2.setFirstName("User");
        req2.setLastName("Two");
        req2.setEnabled(true);
        req2.setRole("USER");
        Long userId1 = objectMapper.readTree(mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
                .andReturn().getResponse().getContentAsString()).get("id").asLong();
        Long userId2 = objectMapper.readTree(mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andReturn().getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/users/" + userId1))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("user2"));
    }

    @Test
    void createUser_shouldFailValidation() throws Exception {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername(""); // invalid
        req.setPassword("123"); // too short
        req.setEmail("not-an-email"); // invalid
        req.setFirstName(""); // invalid
        req.setLastName(""); // invalid
        req.setEnabled(true);
        req.setRole(""); // invalid

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.role").exists());
    }
}

