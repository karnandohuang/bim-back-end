package com.inventory.services.utils.exceptions.security;

public class TokenParsingFailedException extends RuntimeException {
    public TokenParsingFailedException() {
        super("Failed parsing token!");
    }
}
