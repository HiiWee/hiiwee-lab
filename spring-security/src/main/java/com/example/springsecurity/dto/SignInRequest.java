package com.example.springsecurity.dto;

import lombok.Getter;

@Getter
public class SignInRequest {

    private String name;
    private String password;

    public SignInRequest() {
    }

    public SignInRequest(final String name, final String password) {
        this.name = name;
        this.password = password;
    }
}
