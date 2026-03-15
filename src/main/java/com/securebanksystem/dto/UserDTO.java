package com.securebanksystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String mobileNumber;

    private String address;

    private String accountStatus;

    private String role;

    private String panNumber;

    private String aadhaarNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

}