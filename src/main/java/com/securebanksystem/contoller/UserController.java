    package com.securebanksystem.contoller;

    import com.securebanksystem.dto.LoginRequest;
    import com.securebanksystem.dto.LoginResponse;
    import com.securebanksystem.dto.UserDTO;
    import com.securebanksystem.model.User;
    import com.securebanksystem.respnse.ApiResponse;
    import com.securebanksystem.service.UserService;
    import com.securebanksystem.uitl.JwtUtils;
    import jakarta.validation.Valid;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/users")
    public class UserController {

        private final UserService userService;

        private final PasswordEncoder passwordEncoder;

        private final JwtUtils jwtUtils;

        public UserController(UserService userService,PasswordEncoder passwordEncoder,JwtUtils jwtUtils) {
            this.userService = userService;
            this.passwordEncoder=passwordEncoder;
            this.jwtUtils=jwtUtils;
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


        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
            UserDTO user = userService.findByEmail(loginRequest.getEmail());

            if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

                // CHECK STATUS BEFORE ISSUING TOKEN
                if (!"ACTIVE".equals(user.getAccountStatus())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Please verify your account via OTP first.");
                }

                String token = jwtUtils.generateToken(user.getEmail());
                return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), "Login Successful"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Email or Password");
        }
    }