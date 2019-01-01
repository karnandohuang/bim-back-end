package com.inventory.services.member;

import com.inventory.services.utils.exceptions.auth.FailedToLoginException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;

public interface MemberService {
    String authenticateUser(String email, String password) throws FailedToLoginException;

    String getMemberRole(String email);

    UserDetails getLoggedInUser(@AuthenticationPrincipal Principal principal);
}
