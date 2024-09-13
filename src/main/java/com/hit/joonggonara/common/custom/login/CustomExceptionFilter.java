package com.hit.joonggonara.common.custom.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.ErrorCode;
import com.hit.joonggonara.common.error.errorCode.BaseErrorCode;
import com.hit.joonggonara.dto.response.ApiExceptionResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class CustomExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            filterChain.doFilter(request, response);
        }catch (CustomException ex){
            sendErrorResponse(response, ex);
        }catch (Exception ex){
            ErrorCode errorCode = BaseErrorCode.INTERNAL_SERVER_ERROR;
            CustomException exception = new CustomException(errorCode);
            sendErrorResponse(response,exception);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, CustomException ex) throws IOException {
        ErrorCode errorCode = ex.getErrorCode();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiExceptionResponse.of(errorCode)));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
