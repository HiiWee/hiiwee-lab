package com.example.springsecurity.service;

import com.example.springsecurity.domain.RefreshToken;
import com.example.springsecurity.dto.AuthInfo;
import com.example.springsecurity.dto.SignInRequest;
import com.example.springsecurity.dto.TokenResponse;
import com.example.springsecurity.support.token.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder managerBuilder;


    public AuthService(final RefreshTokenService refreshTokenService, final JwtTokenProvider tokenProvider,
                       final AuthenticationManagerBuilder managerBuilder) {
        this.refreshTokenService = refreshTokenService;
        this.tokenProvider = tokenProvider;
        this.managerBuilder = managerBuilder;
    }

    /*
        UserDetailsService의 구현체인 CustomUserDetailService.loadUserByUsername을 실행하여 UserDetails 구현체인 User를 받아오고 authenticationToken과 비교하여
        사용자가 입력한 password와 조회한 password를 비교하여 인증합니다.
     */
    @Transactional
    public TokenResponse signin(final SignInRequest signInRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                signInRequest.getName(), signInRequest.getPassword());

        Authentication authenticate = managerBuilder.getObject()
                .authenticate(authenticationToken);
        String accessToken = tokenProvider.createAccessToken(authenticate);
        String refreshToken = tokenProvider.createRefreshToken();
        refreshTokenService.saveToken(Long.parseLong(authenticate.getName()), refreshToken);
        return new TokenResponse(accessToken, refreshToken);
    }

    public void matches(final Long memberId, final String token) {
        RefreshToken savedToken = refreshTokenService.findRefreshTokenById(memberId);

        if (!tokenProvider.validateToken(savedToken.getToken())) {
            refreshTokenService.deleteToken(savedToken.getMemberId());
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        savedToken.validateSameToken(token);
    }

    public String reissueAccessToken(final AuthInfo authInfo, final String refreshToken) {
        matches(authInfo.getId(), refreshToken);
        return tokenProvider.createAccessToken(authInfo);
    }
}
