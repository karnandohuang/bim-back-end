package com.inventory.services.utils.exceptions.auth;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String email) {
        super("Member of email : " + email + " is not exist!");
    }
}
