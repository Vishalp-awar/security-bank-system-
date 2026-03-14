package com.securebanksystem.mapper;

import com.securebanksystem.dto.UserDTO;
import com.securebanksystem.model.User;

public class UserMapper {

    private UserMapper() {

    }

    public static UserDTO toDTO(User user) {

        if (user == null) {
            return null;
        }

        return new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getMobileNumber(), user.getAddress(), user.getAccountStatus());
    }

    public static User toEntity(UserDTO dto) {

        if (dto == null) {
            return null;
        }

        User user = new User();

        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setMobileNumber(dto.getMobileNumber());
        user.setAddress(dto.getAddress());
        user.setAccountStatus(dto.getAccountStatus());

        return user;
    }
}