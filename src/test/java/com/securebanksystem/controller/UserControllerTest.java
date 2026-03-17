//package com.securebanksystem.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.securebanksystem.dto.UserDTO;
//import com.securebanksystem.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(UserController.class)
//// addFilters = false disables Spring Security so you don't get 401/403 errors during unit testing
//class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean // Use @MockBean if you are on Spring Boot version < 3.4
//    private UserService userService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void testSaveUser() throws Exception {
//        // --- ARRANGE ---
//        UserDTO inputUser = new UserDTO();
//        inputUser.setFirstName("Vishal");
//        inputUser.setEmail("vishal@test.com");
//
//        UserDTO savedUser = new UserDTO();
//        savedUser.setId(1L);
//        savedUser.setFirstName("Vishal");
//        savedUser.setEmail("vishal@test.com");
//
//        // Stub the service call
//        Mockito.when(userService.saveUser(any(UserDTO.class))).thenReturn(savedUser);
//
//        // --- ACT & ASSERT ---
//        mockMvc.perform(post("/api/users") // Check if your Controller has @RequestMapping("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(inputUser)))
//                .andExpect(status().isCreated()) // Expecting 201 Created
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Registered Successfully"))
//                .andExpect(jsonPath("$.data.firstName").value("Vishal"));
//    }
//}


package com.securebanksystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securebanksystem.contoller.UserController;
import com.securebanksystem.dto.UserDTO;
import com.securebanksystem.exception.DuplicateEmailException;
import com.securebanksystem.exception.UserNotFoundException;
import com.securebanksystem.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Use @MockBean if you are on Spring Boot version < 3.4
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSaveUser() throws Exception {
        // --- ARRANGE ---
        UserDTO inputUser = new UserDTO();
        inputUser.setFirstName("Vishal");
        inputUser.setEmail("vishal@test.com");

        UserDTO savedUser = new UserDTO();
        savedUser.setId(1L);
        savedUser.setFirstName("Vishal");
        savedUser.setEmail("vishal@test.com");

        // Stub the service call
        Mockito.when(userService.saveUser(any(UserDTO.class))).thenReturn(savedUser);

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/users") // Check if your Controller has @RequestMapping("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated()) // Expecting 201 Created
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registered Successfully"))
                .andExpect(jsonPath("$.data.firstName").value("Vishal"));
    }

    @Test
    void TestGetById() throws Exception {
        UserDTO inputUser = new UserDTO();
        inputUser.setId(1L);
        inputUser.setFirstName("Vishal");
        inputUser.setEmail("vishal@test.com");

        Mockito.when(userService.findById(1L)).thenReturn(inputUser);

        // 2. Act & Assert
        mockMvc.perform(get("/users/{id}", 1L) // Pass the ID as a template variable
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User Fetched Successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.firstName").value("Vishal"));
    }

    @Test
    void TestGetALlUsers() throws Exception {
        List<UserDTO> userDTOList = new ArrayList<>(); // ArrayList is more common for DTOs

        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setFirstName("Vishal");
        user1.setEmail("vishal@test.com");

        UserDTO user2 = new UserDTO();
        user2.setId(2L); // Unique ID
        user2.setFirstName("Mahesh");
        user2.setEmail("mahesh@test.com");

        userDTOList.add(user1);
        userDTOList.add(user2);

        Mockito.when(userService.getAllUsers()).thenReturn(userDTOList);


        // 2. Act & Assert
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Fetched All Users!"))
                // Verify the list size inside the 'data' field
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(2)))
                // Verify specific elements
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].firstName").value("Vishal"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].firstName").value("Mahesh"));
    }

    @Test
    void testUpdateUser() throws Exception {
        // 1. Arrange
        Long userId = 2L;

        UserDTO inputDto = new UserDTO();
        inputDto.setId(userId);
        inputDto.setFirstName("Vishal");
        inputDto.setLastName("Pawar");

        UserDTO updatedUser = new UserDTO();
        updatedUser.setId(userId);
        updatedUser.setFirstName("Vishal");
        updatedUser.setLastName("Pawar");

        // Stubbing: match the ID and the body
        Mockito.when(userService.updateById(Mockito.eq(userId), any(UserDTO.class)))
                .thenReturn(updatedUser);

        // 2. Act & Assert
        mockMvc.perform(put("/users/{id}", userId) // Pass ID here
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto))) // Send JSON body here
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Updated User SuccessFully"))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.lastName").value("Pawar"));
    }

    @Test
    void testDeleteUser() throws Exception {
        // 1. Arrange
        Long userId = 1L;

        // For void methods, we tell Mockito to do nothing
        Mockito.doNothing().when(userService).deleteUser(userId);

        // 2. Act & Assert
        mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Expects 204 No Content

        // 3. Verify - This is CRITICAL for Delete tests
        // It proves the controller actually told the service to delete the user
        Mockito.verify(userService, Mockito.times(1)).deleteUser(userId);
    }

    @Test
    void testHandleUserNotFoundException() throws Exception {
        // 1. Arrange: Force the service to throw the exception
        Mockito.when(userService.findById(99L))
                .thenThrow(new UserNotFoundException("User not found with id: 99"));

        // 2. Act & Assert
        mockMvc.perform(get("/users/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found with id: 99"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testHandleDuplicateEmailException() throws Exception {
        // 1. Arrange
        UserDTO newUser = new UserDTO();
        newUser.setEmail("exists@test.com");

        Mockito.when(userService.saveUser(any(UserDTO.class)))
                .thenThrow(new DuplicateEmailException("Email already registered"));

        // 2. Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isConflict()) // 409
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

}