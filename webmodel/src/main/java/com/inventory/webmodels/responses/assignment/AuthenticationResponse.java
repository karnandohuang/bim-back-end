package com.inventory.webmodels.responses.assignment;

import lombok.Data;

@Data
public class AuthenticationResponse {

    private String username;
    private String token;
}
