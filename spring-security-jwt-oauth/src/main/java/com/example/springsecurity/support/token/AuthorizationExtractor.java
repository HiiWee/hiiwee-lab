package com.example.springsecurity.support.token;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import javax.servlet.http.HttpServletRequest;

/**
 * JWT Token Parsing util
 */
public class AuthorizationExtractor {

    public static final String BEARER_TYPE = "Bearer";

    public static String extractAccessToken(final HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        return extract(header);
    }

    public static String extractRefreshToken(final HttpServletRequest request) {
        String header = request.getHeader("Refresh-Token");
        return extract(header);

    }

    private static String extract(final String header) {
        if (header.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
            return header.substring(BEARER_TYPE.length())
                    .trim();
        }
        return null;
    }
}
