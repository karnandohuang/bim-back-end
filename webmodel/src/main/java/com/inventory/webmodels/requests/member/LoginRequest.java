package com.inventory.webmodels.requests.member;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
