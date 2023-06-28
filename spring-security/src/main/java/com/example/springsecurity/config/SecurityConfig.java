package com.example.springsecurity.config;

import com.example.springsecurity.advice.ErrorResponse;
import com.example.springsecurity.support.token.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(final JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .antMatchers("/favicon.ico");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 설정 Disable
        http.csrf().disable()
                .cors().and()
                .exceptionHandling()
                // TODO 인증, 인간 실패시 예외 처리를 @ExceptionHandler를 통해 처리할 수 있도록 변경
                // TODO 각 코드에 설명 주석 달기
                // 인증 실패시 예외 처리
                .authenticationEntryPoint((request, response, authException) -> {
                    ErrorResponse errorResponse = new ErrorResponse("필터 반환 : 인증된 사용자가 아닙니다.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    OutputStream responseStream = response.getOutputStream();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(responseStream, errorResponse);
                    responseStream.flush();
                })
                // 인가 실패시 예외 처리 (허락되지 않은 접근)
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    ErrorResponse errorResponse = new ErrorResponse("필터 반환 : 해당 사용자는 접근할 수 없습니다.");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    ServletOutputStream responseStream = response.getOutputStream();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(responseStream, errorResponse);
                    responseStream.flush();
                })
                .and()
                // JWT를 사용하므로 세션 사용하지 않음
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 로그인, 회원가입 API는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll 설정
                .and()
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/members/signup").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new JwtSecurityConfig(jwtTokenProvider))
                .and()
                .formLogin().disable();
        return http.build();
    }
}
