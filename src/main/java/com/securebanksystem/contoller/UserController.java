package com.securebanksystem.contoller;

import com.securebanksystem.dto.UserDTO;
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
    public ResponseEntity<ApiResponse<UserDTO>> saveUser(@Valid @RequestBody UserDTO user) {
        UserDTO savedUser = userService.saveUser(user);
        return ResponseEntity.status(201).body(new ApiResponse<>(true,"Registered Successfully",savedUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true,"User Fetched Successfully",userService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        return ResponseEntity.ok(new ApiResponse<>(true,"Fetched All Users!",userService.getAllUsers()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@Valid @PathVariable Long id, @RequestBody UserDTO user) {
        return ResponseEntity.ok(new ApiResponse<>(true,"Updated User SuccessFully", userService.updateById(id, user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}