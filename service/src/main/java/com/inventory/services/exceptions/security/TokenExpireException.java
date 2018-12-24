package com.inventory.services.exceptions.security;

public class TokenExpireException extends RuntimeException {
    public TokenExpireException() {
        super("Token has expired. Please login");
    }
}
