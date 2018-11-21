package com.inventory.webmodels.requests.employee;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
