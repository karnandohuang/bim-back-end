package com.inventory.services.member;

import com.inventory.services.exceptions.auth.FailedToLoginException;

public interface MemberService {
    String authenticateUser(String email, String password) throws FailedToLoginException;
}
