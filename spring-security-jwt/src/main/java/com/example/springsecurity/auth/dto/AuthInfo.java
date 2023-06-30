package com.example.springsecurity.auth.dto;

import com.example.springsecurity.member.domain.Role;
import java.util.Arrays;
import lombok.Getter;

@Getter
public class AuthInfo {

    private final Long id;
    private final String role;

    public AuthInfo(final Long id, final String role) {
        this.id = id;
        this.role = role;
    }

    public static AuthInfo of(final Long id, final String totalRole) {
        return new AuthInfo(id, parseRole(totalRole));
    }

    private static String parseRole(final String totalRole) {
        return Arrays.stream(totalRole.split(","))
                .filter(Role::containsValue)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("사용자의 역할(role)이 존재하지 않습니다."));
    }
}
