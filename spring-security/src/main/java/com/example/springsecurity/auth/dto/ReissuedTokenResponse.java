package com.example.springsecurity.auth.dto;

import lombok.Getter;

@Getter
public class ReissuedTokenResponse {

    private final String accessToken;

    public ReissuedTokenResponse(final String accessToken) {
        this.accessToken = accessToken;
    }
}
