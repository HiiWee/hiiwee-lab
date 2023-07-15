package com.example.springsecurity.config.oauth2.handler;

import com.example.springsecurity.config.oauth2.repository.CookieAuthorizationRequestRepository;
import com.example.springsecurity.support.utils.CookieUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.util.UriComponentsBuilder;

public class Oauth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";

    private final CookieAuthorizationRequestRepository authorizationRequestRepository;

    public Oauth2AuthenticationFailureHandler(
            final CookieAuthorizationRequestRepository authorizationRequestRepository) {
        this.authorizationRequestRepository = authorizationRequestRepository;
    }

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
                                        final AuthenticationException exception) throws IOException, ServletException {
        String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("/");

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
