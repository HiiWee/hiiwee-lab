package com.example.springsecurity.auth.service;

import com.example.springsecurity.auth.domain.RefreshToken;
import com.example.springsecurity.auth.respository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(final RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void saveToken(final Long memberId, final String token) {
        deleteToken(memberId);
        RefreshToken refreshToken = new RefreshToken(memberId, token);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteToken(final Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }

    public RefreshToken findRefreshTokenByMemberId(final Long memberId) {
        return refreshTokenRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));
    }
}
