package com.example.springsecurity.config.oauth2.handler;

import com.example.springsecurity.auth.domain.RefreshToken;
import com.example.springsecurity.auth.respository.RefreshTokenRepository;
import com.example.springsecurity.config.oauth2.repository.CookieAuthorizationRequestRepository;
import com.example.springsecurity.support.token.JwtTokenProvider;
import com.example.springsecurity.support.utils.CookieUtils;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class Oauth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";

    @Value("${redirect-url}")
    private String redirectUriProperty;

    private final JwtTokenProvider tokenProvider;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public Oauth2AuthenticationSuccessHandler(final JwtTokenProvider tokenProvider,
                                              final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository,
                                              final RefreshTokenRepository refreshTokenRepository) {
        this.tokenProvider = tokenProvider;
        this.cookieAuthorizationRequestRepository = cookieAuthorizationRequestRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) {
        String redirectUrl = findRedirectUrl(request);
        URI clientUrl = URI.create(redirectUrl);
        URI serverUrl = URI.create(redirectUriProperty);
        if (!clientUrl.getHost().equalsIgnoreCase(serverUrl.getHost())) {
            throw new IllegalArgumentException("Unauthorized Redirect URL");
        }

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken();
        saveRefreshToken(refreshToken, Long.valueOf(authentication.getName()));

        return UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("token", accessToken)
                .queryParam("refresh", refreshToken)
                .build()
                .toUriString();
    }

    private static String findRedirectUrl(final HttpServletRequest request) {
        return CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElseThrow(() -> new IllegalArgumentException("redirect url을 반드시 입력해야 합니다."));
    }

    private void saveRefreshToken(final String token, final Long memberId) {
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByMemberId(memberId);
        if (existingRefreshToken.isPresent()) {
            existingRefreshToken.get().updateToken(token);
            refreshTokenRepository.save(existingRefreshToken.get());
            return;
        }
        RefreshToken newRefreshToken = new RefreshToken(memberId, token);
        refreshTokenRepository.save(newRefreshToken);
    }

    private void clearAuthenticationAttributes(final HttpServletRequest request, final HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
