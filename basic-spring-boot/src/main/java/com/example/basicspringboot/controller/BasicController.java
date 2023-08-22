package com.example.basicspringboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class BasicController {

    private final ObjectMapper objectMapper;

    public BasicController(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * writeValue 이후 setStatus를 하면 적용되지 않는 이유
     */
    @GetMapping("/basic")
    public void basic(final HttpServletResponse response) throws IOException {
        MessageResponse messageResponse = new MessageResponse("response message to Response Body.");
        objectMapper.writeValue(response.getOutputStream(), messageResponse);
        response.setStatus(400);
    }
}
