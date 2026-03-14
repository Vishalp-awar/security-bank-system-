package com.securebanksystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {

    private int id;

    private String firstName;

    private String lastName;

    private String email;

    private long mobileNumber;

    private String address;

    private String accountStatus;

}