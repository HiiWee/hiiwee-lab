package com.example.springsecurity.config.oauth2.exception;

public class Oauth2ProviderNotFoundException extends RuntimeException {

    public Oauth2ProviderNotFoundException(final String message) {
        super(message);
    }
}
