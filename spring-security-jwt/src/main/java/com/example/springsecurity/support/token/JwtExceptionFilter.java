package com.example.springsecurity.support.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.jsonwebtoken.JwtException;
import java.io.IOException;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JwtFilter에서 발생하는 예외를 인증 예외로 합쳐서 응답하지 않고 각각 응답하도록 하는 예외 필터
 */
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper;

    public JwtExceptionFilter(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            setErrorResponse(response, e);
        }
    }

    private void setErrorResponse(final HttpServletResponse response, final JwtException e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> responseBody = Maps.newHashMap();
        responseBody.put("message", e.getMessage());
        mapper.writeValue(response.getOutputStream(), responseBody);
    }
}
