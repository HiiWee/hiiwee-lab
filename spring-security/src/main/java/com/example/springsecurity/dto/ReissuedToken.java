package com.example.springsecurity.dto;

import lombok.Getter;

@Getter
public class ReissuedToken {

    private String accessToken;

    public ReissuedToken(final String accessToken) {
        this.accessToken = accessToken;
    }
}
