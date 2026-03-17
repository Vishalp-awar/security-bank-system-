package com.securebanksystem.service;

import com.securebanksystem.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO saveUser(UserDTO user);

    UserDTO findById(Long id);

    UserDTO updateById(Long id, UserDTO user);

    List<UserDTO> getAllUsers();

    void deleteUser(Long id);
}
