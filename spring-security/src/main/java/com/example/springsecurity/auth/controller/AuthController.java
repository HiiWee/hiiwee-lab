package com.example.springsecurity.auth.controller;

import com.example.springsecurity.auth.dto.AuthInfo;
import com.example.springsecurity.auth.dto.ReissuedTokenResponse;
import com.example.springsecurity.auth.dto.SignInRequest;
import com.example.springsecurity.auth.dto.TokenResponse;
import com.example.springsecurity.auth.service.AuthService;
import com.example.springsecurity.support.token.AuthorizationExtractor;
import com.example.springsecurity.support.token.Login;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public TokenResponse login(@RequestBody final SignInRequest signInRequest) {
        return authService.login(signInRequest);
    }

    @GetMapping("/refresh")
    public ResponseEntity<ReissuedTokenResponse> refresh(@Login final AuthInfo authInfo,
                                                         final HttpServletRequest request) {
        validateExistHeader(request);
        String refreshToken = AuthorizationExtractor.extractRefreshToken(request);
        ReissuedTokenResponse reissuedTokenResponse = authService.reissueAccessToken(authInfo, refreshToken);
        return ResponseEntity.ok(reissuedTokenResponse);
    }

    @GetMapping("/signout")
    public ResponseEntity<Void> logout(@Login AuthInfo authInfo) {
        authService.logout(authInfo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    private void validateExistHeader(final HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshTokenHeader = request.getHeader("Refresh-Token");
        if (Objects.isNull(authorizationHeader) || Objects.isNull(refreshTokenHeader)) {
            throw new IllegalArgumentException("토큰이 존재하지 않습니다.");
        }
    }
}
