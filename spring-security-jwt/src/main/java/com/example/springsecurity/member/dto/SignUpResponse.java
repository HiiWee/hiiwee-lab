package com.example.springsecurity.member.dto;

import lombok.Getter;

@Getter
public class SignUpResponse {

    private Long memberId;

    private SignUpResponse() {
    }

    public SignUpResponse(final Long memberId) {
        this.memberId = memberId;
    }
}
