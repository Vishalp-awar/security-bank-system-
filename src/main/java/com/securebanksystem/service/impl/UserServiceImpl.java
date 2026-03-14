package com.securebanksystem.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    @Override
    public UserDTO findById(int id) {

        log.info("Fetching user with id {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return UserMapper.toDTO(user);
    }

    @CachePut(value = "users", key = "#id")
    @Override
    public User updateById(int id, User user) {

        User existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        updateUserFields(existingUser, user);

        return userRepository.save(existingUser);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @CacheEvict(value = "users", key = "#id")
    @Override
    public void deleteUser(int id) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
    }

    private void updateUserFields(User existingUser, User user) {
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setMobileNumber(user.getMobileNumber());
        existingUser.setAddress(user.getAddress());
        existingUser.setUpdatedAt(LocalDateTime.now());
        existingUser.setAccountStatus(user.getAccountStatus());
    }

}
