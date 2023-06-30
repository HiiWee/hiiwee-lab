package com.example.springsecurity.member.domain;

import java.util.Arrays;

public enum Role {
    USER, ADMIN;

    public static boolean containsValue(final String value) {
        return Arrays.stream(values())
                .anyMatch(role -> role.name().equals(value));
    }
}
