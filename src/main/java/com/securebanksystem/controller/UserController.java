package com.securebanksystem.controller;

import com.securebanksystem.dto.UserDTO;
import com.securebanksystem.model.User;
import com.securebanksystem.respnse.ApiResponse;
import com.securebanksystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> saveUser(@Valid @RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(201).body(new ApiResponse<>(true,"Registered Successfully",savedUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getById(@PathVariable int id) {
        return ResponseEntity.ok(new ApiResponse<>(true,"User Fetched Successfully",userService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(new ApiResponse<>(true,"Fetched All Users!",userService.getAllUsers()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@Valid @PathVariable int id, @RequestBody User user) {
        return ResponseEntity.ok(new ApiResponse<>(true,"Updated User SuccessFully", userService.updateById(id, user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}