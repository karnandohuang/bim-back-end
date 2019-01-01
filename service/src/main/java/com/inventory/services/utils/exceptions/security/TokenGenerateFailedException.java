package com.inventory.services.utils.exceptions.security;

public class TokenGenerateFailedException extends RuntimeException {
    public TokenGenerateFailedException() {
        super("Failed generate token!");
    }
}
