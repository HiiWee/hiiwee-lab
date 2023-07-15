package com.example.springsecurity.config;

import com.example.springsecurity.support.token.JwtExceptionFilter;
import com.example.springsecurity.support.token.JwtFilter;
import com.example.springsecurity.support.token.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * JWT관련 필터들을 Sercurity 설정에 적용하기 위함 UsernamePasswordAuthenticationFilter보다 먼저 JwtFilter가 실행되도록 합니다.
 */
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper mapper;

    public JwtSecurityConfig(final JwtTokenProvider jwtTokenProvider, final ObjectMapper mapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.mapper = mapper;
    }

    @Override
    public void configure(final HttpSecurity http) {
        http.addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtExceptionFilter(mapper), JwtFilter.class);
    }
}
