package com.hit.joonggonara.common.custom.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.joonggonara.dto.response.ApiExceptionResponse;
import com.hit.joonggonara.common.error.ErrorCode;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        ErrorCode errorCode = UserErrorCode.ACCESS_DENIED_ERROR;
        ApiExceptionResponse apiExceptionResponse = ApiExceptionResponse.of(errorCode);

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(apiExceptionResponse));
        response.getWriter().flush();
        response.getWriter().close();

    }
}
