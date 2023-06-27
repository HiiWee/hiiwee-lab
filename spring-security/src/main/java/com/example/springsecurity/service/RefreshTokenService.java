package com.example.springsecurity.service;

import com.example.springsecurity.domain.RefreshToken;
import com.example.springsecurity.respository.RefreshTokenRepository;
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

    public RefreshToken findRefreshTokenById(final Long id) {
        System.out.println(id);
        return refreshTokenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));
    }
}
