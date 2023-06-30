package com.example.springsecurity.config.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * 인가 예외가 발생하면 예외 이후 동작으로 handle()메서드를 실행 시킵니다.
 */
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    public RestAccessDeniedHandler(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response,
                       final AccessDeniedException accessDeniedException) throws IOException {
        HashMap<String, Object> responseBody = new HashMap<>();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        responseBody.put("message", "필터 반환 : 해당 사용자는 접근할 수 없습니다.");
        mapper.writeValue(response.getOutputStream(), responseBody);
    }
}
