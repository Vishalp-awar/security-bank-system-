package com.securebanksystem.service.impl;

import com.securebanksystem.dto.UserDTO;
import com.securebanksystem.exception.DuplicateEmailException;
import com.securebanksystem.exception.UserNotFoundException;
import com.securebanksystem.mapper.UserMapper;
import com.securebanksystem.model.User;
import com.securebanksystem.repository.UserRepository;
import com.securebanksystem.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO saveUser(UserDTO user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        User userEntity = UserMapper.toEntity(user);
        user.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        User savedUser = userRepository.save(userEntity);
        return UserMapper.toDTO(savedUser);
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
    public UserDTO updateById(int id, UserDTO user) {

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
    public void deleteUser(int id) {

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
    }

}
