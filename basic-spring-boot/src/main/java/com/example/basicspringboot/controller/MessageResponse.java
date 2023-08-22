package com.example.basicspringboot.controller;

import lombok.Getter;

@Getter
public class MessageResponse {

    private String message;

    private MessageResponse() {
    }

    public MessageResponse(final String message) {
        this.message = message;
    }
}
