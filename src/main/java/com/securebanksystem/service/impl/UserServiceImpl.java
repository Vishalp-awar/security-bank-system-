package com.securebanksystem.service.impl;

import com.securebanksystem.dto.UserDTO;
import com.securebanksystem.mapper.UserMapper;
import com.securebanksystem.model.User;
import com.securebanksystem.repository.UserRepository;
import com.securebanksystem.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) {
        // 1. Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            // You can throw a custom exception here
            throw new RuntimeException("Email " + user.getEmail() + " is already registered!");
        }

        // 2. Set timestamps if they are null
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
    @Cacheable(value = "users", key = "#id")
    @Override
    public UserDTO findById(int id) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        return UserMapper.toDTO(user);
    }

    @CachePut(value = "users", key = "#id")
    @Override
    public User updateById(int id, User user) {

        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        updateUserFields(existingUser, user);

        return userRepository.save(existingUser);
    }

    @Override
    @Cacheable(value = "users", key = "'all'")
    public List<User> getAllUsers() {
        List<User> user = userRepository.findAll();
        if (!user.isEmpty()) {
            return user;
        } else {

            return List.of();
        }
    }

    @CacheEvict(value = "users", key = "#id")
    @Override
    public void deleteUser(int id) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);

    }

    private void updateUserFields(User existingUser, User user) {
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setMobileNumber(user.getMobileNumber());
        existingUser.setAddress(user.getAddress());
        existingUser.setAccountStatus(user.getAccountStatus());
    }

}
