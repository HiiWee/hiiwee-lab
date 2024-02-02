package com.example.springsecurity.member.domain;

import java.util.Arrays;

public enum Role {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String value;

    Role(final String value) {
        this.value = value;
    }

    public static boolean containsValue(final String value) {
        return Arrays.stream(values())
                .anyMatch(role -> role.value.equals(value));
    }

    public String getValue() {
        return value;
    }
}
