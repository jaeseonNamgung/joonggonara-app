package com.hit.joonggonara.controller.exception;

import com.hit.joonggonara.dto.response.ApiExceptionResponse;
import com.hit.joonggonara.dto.response.ValidationResponse;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.ErrorCode;
import com.hit.joonggonara.common.error.errorCode.BaseErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, ValidationResponse.of(status.value(), ex.getBindingResult()),headers,status, request);
    }


    @ExceptionHandler
    public ResponseEntity<Object> customException(CustomException customException, WebRequest webRequest){
        return handleExceptionInternal(customException, customException.getErrorCode(), webRequest);
    }

    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception ex, WebRequest webRequest){
        return handleExceptionInternal(ex, BaseErrorCode.INTERNAL_SERVER_ERROR, webRequest);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception  ex, ErrorCode errorCode, WebRequest webRequest) {
        return handleExceptionInternal(ex, errorCode, HttpHeaders.EMPTY,errorCode.getHttpStatus(), webRequest);
    }

    // Validation Exception
    protected ResponseEntity<Object> handleExceptionInternal(Exception  ex, ValidationResponse validationResponse,HttpHeaders headers, HttpStatusCode status, WebRequest webRequest) {
        return super.handleExceptionInternal(ex, validationResponse, headers, status, webRequest);
    }

    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, ErrorCode errorCode, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        return super.handleExceptionInternal(
                ex,
                ApiExceptionResponse.of(errorCode),
                headers,
                statusCode,
                request);
    }
}
