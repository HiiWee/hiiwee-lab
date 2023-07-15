package com.example.springsecurity.support.utils;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

public class CookieUtils {


    public static Optional<Cookie> getCookie(final HttpServletRequest request, final String name) {
        Cookie[] cookies = request.getCookies();
        if (Objects.isNull(cookies)) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findAny();
    }

    public static void addCookie(final HttpServletResponse response, final String name, final String value,
                                 final int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    public static void deleteCookie(final HttpServletRequest request, final HttpServletResponse response,
                                    final String name) {
        Cookie[] cookies = request.getCookies();
        if (Objects.isNull(cookies) || cookies.length == 0) {
            return;
        }
        Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .findAny()
                .ifPresent(cookie -> {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                });
    }

    public static String serialize(final Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(final Cookie cookie, final Class<T> clazz) {
        return clazz.cast(
                SerializationUtils.deserialize(Base64.getUrlDecoder()
                        .decode(cookie.getValue()))
        );
    }
}
