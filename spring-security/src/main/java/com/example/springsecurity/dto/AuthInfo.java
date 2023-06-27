package com.example.springsecurity.dto;

import lombok.Getter;

@Getter
public class AuthInfo {

    private final Long id;
    private final String role;

    public AuthInfo(final Long id, final String role) {
        this.id = id;
        this.role = role;
    }
}
