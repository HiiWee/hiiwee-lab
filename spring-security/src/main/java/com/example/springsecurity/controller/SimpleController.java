package com.example.springsecurity.controller;

import com.example.springsecurity.dto.AuthInfo;
import com.example.springsecurity.dto.ReissuedToken;
import com.example.springsecurity.dto.SignInRequest;
import com.example.springsecurity.dto.SignUpRequest;
import com.example.springsecurity.dto.TokenResponse;
import com.example.springsecurity.service.AuthService;
import com.example.springsecurity.service.MemberService;
import com.example.springsecurity.support.token.AuthorizationExtractor;
import com.example.springsecurity.support.token.Login;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

    private final MemberService memberService;
    private final AuthService authService;

    public SimpleController(final MemberService memberService, final AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<Long> signup(@RequestBody final SignUpRequest signUpRequest) {
        Long memberId = memberService.signup(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberId);
    }

    @PostMapping("/auth/signin")
    public TokenResponse signIn(@RequestBody final SignInRequest signInRequest) {
        return authService.signin(signInRequest);
    }

    // TODO: refresh token 재발급 절차 완성
    @GetMapping("/refresh")
    public ReissuedToken refresh(@Login final AuthInfo authInfo, final HttpServletRequest request) {
        System.out.println(authInfo.getRole());
        System.out.println(authInfo.getId());
        validateExistHeader(request);
        String refreshToken = AuthorizationExtractor.extractRefreshToken(request);
        return new ReissuedToken(authService.reissueAccessToken(authInfo, refreshToken));
    }

    private void validateExistHeader(final HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshTokenHeader = request.getHeader("Refresh-Token");
        if (Objects.isNull(authorizationHeader) || Objects.isNull(refreshTokenHeader)) {
            throw new IllegalArgumentException("토큰이 존재하지 않습니다.");
        }
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
