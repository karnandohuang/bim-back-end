package com.inventory.services.member;

import com.inventory.services.utils.exceptions.auth.FailedToLoginException;
import org.springframework.security.core.userdetails.UserDetails;

public interface MemberService {
    String authenticateUser(String email, String password) throws FailedToLoginException;

    String getMemberRole(String email);

    UserDetails getLoggedInUser(Object principal);
}
