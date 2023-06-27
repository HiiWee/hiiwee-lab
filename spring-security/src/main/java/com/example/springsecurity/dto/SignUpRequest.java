package com.example.springsecurity.dto;

import lombok.Getter;

@Getter
public class SignUpRequest {

    private String name;
    private String password;

    public SignUpRequest(final String name) {
        this.name = name;
    }

    public SignUpRequest(final String name, final String password) {
        this.name = name;
        this.password = password;
    }
}
