package com.inventory.services.utils.exceptions.security;

public class TokenExpireException extends RuntimeException {
    public TokenExpireException() {
        super("Token has expired. Please login");
    }
}
