package com.example.springsecurity.auth.service;

import com.example.springsecurity.auth.domain.RefreshToken;
import com.example.springsecurity.auth.dto.AuthInfo;
import com.example.springsecurity.auth.dto.ReissuedTokenResponse;
import com.example.springsecurity.auth.dto.SignInRequest;
import com.example.springsecurity.auth.dto.TokenResponse;
import com.example.springsecurity.auth.respository.RefreshTokenRepository;
import com.example.springsecurity.support.token.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder managerBuilder;

    public AuthService(final RefreshTokenRepository refreshTokenRepository, final JwtTokenProvider tokenProvider,
                       final AuthenticationManagerBuilder managerBuilder) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProvider = tokenProvider;
        this.managerBuilder = managerBuilder;
    }

    /*
        UserDetailsService의 구현체인 CustomUserDetailService.loadUserByUsername을 실행하여 UserDetails 구현체인 User를 받아오고 authenticationToken과 비교하여
        사용자가 입력한 password와 조회한 password를 비교하여 인증합니다.
     */
    @Transactional
    public TokenResponse login(final SignInRequest signInRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                signInRequest.getName(), signInRequest.getPassword());

        Authentication authenticate = managerBuilder.getObject()
                .authenticate(authenticationToken);
        String accessToken = tokenProvider.createAccessToken(authenticate);
        String refreshToken = tokenProvider.createRefreshToken();
        saveRefreshToken(Long.parseLong(authenticate.getName()), refreshToken);
        return new TokenResponse(accessToken, refreshToken);
    }

    public ReissuedTokenResponse reissueAccessToken(final AuthInfo authInfo, final String refreshToken) {
        matches(authInfo.getId(), refreshToken);
        String reissuedAccessToken = tokenProvider.createAccessToken(authInfo);
        return new ReissuedTokenResponse(reissuedAccessToken);
    }

    @Transactional
    public void logout(final AuthInfo authInfo) {
        refreshTokenRepository.deleteAllByMemberId(authInfo.getId());
    }

    private void saveRefreshToken(final Long memberId, final String token) {
        refreshTokenRepository.deleteAllByMemberId(memberId);
        RefreshToken refreshToken = new RefreshToken(memberId, token);
        refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken findRefreshTokenObject(final Long memberId) {
        return refreshTokenRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));
    }

    private void matches(final Long memberId, final String token) {
        RefreshToken savedToken = findRefreshTokenObject(memberId);
        if (!tokenProvider.validateToken(savedToken.getToken())) {
            refreshTokenRepository.deleteAllByMemberId(savedToken.getMemberId());
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        savedToken.validateSameToken(token);
    }
}
