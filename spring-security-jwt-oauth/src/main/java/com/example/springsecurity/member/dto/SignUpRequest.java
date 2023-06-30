package com.example.springsecurity.member.dto;

import lombok.Getter;

@Getter
public class SignUpRequest {

    private String name;
    private String password;

    public SignUpRequest(final String name, final String password) {
        this.name = name;
        this.password = password;
    }
}
