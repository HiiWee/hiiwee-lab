package com.example.springsecurity.auth.dto;

import lombok.Getter;

@Getter
public class ReissuedTokenResponse {

    private String accessToken;

    private ReissuedTokenResponse() {
    }

    public ReissuedTokenResponse(final String accessToken) {
        this.accessToken = accessToken;
    }
}
