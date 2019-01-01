package com.inventory.helpers;

import com.inventory.webmodels.responses.assignment.AuthenticationResponse;
import org.springframework.stereotype.Component;

@Component
public class MemberHelper extends ModelHelper {

    public AuthenticationResponse getMappedAuthenticationResponse(String email, String token, String role) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setUsername(email);
        response.setToken(token);
        response.setRole(role);
        return response;
    }

}
