package com.example.springsecurity.config;

import com.example.springsecurity.auth.respository.RefreshTokenRepository;
import com.example.springsecurity.config.exception.RestAccessDeniedHandler;
import com.example.springsecurity.config.exception.RestAuthenticationEntryPoint;
import com.example.springsecurity.config.oauth2.handler.Oauth2AuthenticationFailureHandler;
import com.example.springsecurity.config.oauth2.handler.Oauth2AuthenticationSuccessHandler;
import com.example.springsecurity.config.oauth2.repository.CookieAuthorizationRequestRepository;
import com.example.springsecurity.support.token.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 전체적인 Spring Security에 대한 설정을 담당
 */
@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper mapper;

    public SecurityConfig(final JwtTokenProvider jwtTokenProvider,
                          final RefreshTokenRepository refreshTokenRepository, final ObjectMapper mapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.mapper = mapper;
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
    public CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new CookieAuthorizationRequestRepository();
    }

    @Bean
    public Oauth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
        return new Oauth2AuthenticationSuccessHandler(jwtTokenProvider, cookieAuthorizationRequestRepository(),
                refreshTokenRepository);
    }

    @Bean
    public Oauth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler() {
        return new Oauth2AuthenticationFailureHandler(cookieAuthorizationRequestRepository());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 설정 Disable
        http.csrf().disable()
                .formLogin().disable()
                .cors()
                .and()

                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint(mapper))
                .accessDeniedHandler(new RestAccessDeniedHandler(mapper))
                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests()
                .antMatchers("/signin").permitAll()
                .antMatchers("/members/signup").permitAll()
                .antMatchers("/auth/**", "/oauth2/**").permitAll()
                .anyRequest().authenticated()
                .and()

                .apply(new JwtSecurityConfig(jwtTokenProvider, mapper))
                .and()

                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()

                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()

                .successHandler(oauth2AuthenticationSuccessHandler())
                .failureHandler(oauth2AuthenticationFailureHandler());

        return http.build();
    }
}
