package com.example.taskmanagment.handler.customSecurityHandlers;


import com.example.taskmanagment.dto.response.ExceptionResponse;
import com.example.taskmanagment.handler.DefaultExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final DefaultExceptionHandler exceptionHandler;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ResponseEntity<ExceptionResponse> responseEntity = exceptionHandler.handleException(accessDeniedException);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(responseEntity.getStatusCode().value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
    }


}
