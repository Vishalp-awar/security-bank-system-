package com.securebanksystem.dto;

import lombok.Data;

@Data
public class AccountRequest {
    private Long userId;
    private String accountType;
    private Double initialBalance;
}