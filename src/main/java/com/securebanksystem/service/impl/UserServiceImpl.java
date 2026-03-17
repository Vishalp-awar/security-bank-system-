package com.securebanksystem.service.impl;

import com.securebanksystem.dto.AccountRequest;
import com.securebanksystem.dto.UserDTO;
import com.securebanksystem.exception.DuplicateEmailException;
import com.securebanksystem.exception.UserNotFoundException;
import com.securebanksystem.mapper.UserMapper;
import com.securebanksystem.model.User;
import com.securebanksystem.repository.UserRepository;
import com.securebanksystem.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {


    String userServiceUrl = "http://localhost:8081/account";

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate;

    private final AccountRequest accountRequest;
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RestTemplate restTemplate, AccountRequest accountRequest) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate=restTemplate;
        this.accountRequest = accountRequest;
    }

//    @Override
//    public UserDTO saveUser(UserDTO user) {
//        // 1. Validation & Mapping
//        if (userRepository.existsByEmail(user.getEmail())) {
//            throw new DuplicateEmailException("Email already registered");
//        }
//
//        // You need to get the current token from the request context
//        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(token); // Add the "Authorization: Bearer <token>" header
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<AccountRequest> entity = new HttpEntity<>(accountRequest, headers);
//
//        restTemplate.postForEntity(userServiceUrl, entity, Void.class);
//        User userEntity = UserMapper.toEntity(user);
//        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
//        // Set other fields...
//
//        // 2. Save User to DB (Generates the ID)
//        User savedUser = userRepository.save(userEntity);
//
//        // 3. Call Account Service to create the initial account
//        try {
//                AccountRequest accountRequest = new AccountRequest();
//            accountRequest.setUserId(savedUser.getId());
//            accountRequest.setAccountType("SAVINGS"); // Or get from DTO
//            accountRequest.setInitialBalance(0.0);
//
//            restTemplate.postForEntity(userServiceUrl, accountRequest, Void.class);
//        } catch (Exception e) {
//            // IMPORTANT: If account creation fails, we should probably
//            // throw an exception to roll back the @Transactional user save
//            log.error("Failed to create account for user {}: {}", savedUser.getId(), e.getMessage());
//            throw new RuntimeException("User registered but Account creation failed. Rolling back.");
//        }
//
//        return UserMapper.toDTO(savedUser);
//    }
    @Override
    public UserDTO saveUser(UserDTO user) {
        // 1. Basic Validation
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        // 2. Prepare Entity
        User userEntity = UserMapper.toEntity(user);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.setRole(user.getRole());
        userEntity.setPanNumber(user.getPanNumber());
        userEntity.setAadhaarNumber(user.getAadhaarNumber());

        // 1. Save User FIRST
        User savedUser = userRepository.save(userEntity);

        AccountRequest request = new AccountRequest();
        request.setUserId(savedUser.getId()); // This is now a real ID (e.g., 4)
        request.setAccountType("SAVINGS");
        request.setInitialBalance(500.00);

        restTemplate.postForEntity("http://localhost:8081/account", request, Void.class);

        return UserMapper.toDTO(savedUser);
    }
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    @Override
    public UserDTO findById(Long id) {

        log.info("Fetching user with id {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return UserMapper.toDTO(user);
    }

    @CachePut(value = "users", key = "#id")
    @Override
    public UserDTO updateById(Long id, UserDTO user) {

        User existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        updateUserFields(existingUser,user);

        User updatedUser = userRepository.save(existingUser);

        return UserMapper.toDTO(updatedUser);
    }

    @Override
    @Cacheable(value = "users", key = "'all'")
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::toDTO).toList();
    }
    @CacheEvict(value = "users", key = "#id")
    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
    }

    private void updateUserFields(User existingUser, UserDTO user) {
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setMobileNumber(user.getMobileNumber());
        existingUser.setAddress(user.getAddress());
        existingUser.setUpdatedAt(LocalDateTime.now());
        existingUser.setAccountStatus(user.getAccountStatus());
        existingUser.setPassword(user.getPassword());
    }

}
