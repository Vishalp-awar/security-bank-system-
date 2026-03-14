package com.securebanksystem.service;

import com.securebanksystem.dto.UserDTO;
import com.securebanksystem.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    UserDTO findById(int id);

    User updateById(int id, User user);

    List<User> getAllUsers();

    void deleteUser(int id);
}
