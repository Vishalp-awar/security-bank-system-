package com.securebanksystem.dto;

import com.securebanksystem.model.AccountType;
import lombok.Data;

@Data
public class AccountRequest {
    private Long userId;
    private AccountType accountType;
    private Double initialBalance;
    private String firstName;
    private String email;
}