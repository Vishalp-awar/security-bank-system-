package com.securebanksystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private long mobileNumber;

    private String password;

    private String role;

    private String accountStatus;

    private String address;

    private String panNumber;

    private String aadhaarNumber;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}