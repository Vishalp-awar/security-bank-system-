package com.securebanksystem.service;

import com.securebanksystem.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO saveUser(UserDTO user);

    UserDTO findById(int id);

    UserDTO updateById(int id, UserDTO user);

    List<UserDTO> getAllUsers();

    void deleteUser(int id);
}
