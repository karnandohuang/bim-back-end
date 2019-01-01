package com.inventory.webmodels.responses.member;

import lombok.Data;

@Data
public class AuthenticationResponse {

    private String username;
    private String token;
    private String role;
}
