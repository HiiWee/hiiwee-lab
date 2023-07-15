package com.example.springsecurity.auth.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    private Long memberId;

    private String token;

    protected RefreshToken() {
    }

    public RefreshToken(final Long memberId, final String token) {
        this.memberId = memberId;
        this.token = token;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getToken() {
        return token;
    }

    public void validateSameToken(final String token) {
        if (!this.token.equals(token)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
    }

    public void updateToken(final String token) {
        this.token = token;
    }
}
