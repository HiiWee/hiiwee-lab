package com.example.springsecurity.advice;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private String message;

    private ErrorResponse() {
    }

    public ErrorResponse(final String message) {
        this.message = message;
    }
}
