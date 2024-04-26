package com.hit.joonggonara.dto.response;

import com.hit.joonggonara.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record ApiExceptionResponse(
        boolean success,
        int httpStatus,
        String message
) {

    public static ApiExceptionResponse of(ErrorCode errorCode) {
        return new ApiExceptionResponse(false, errorCode.getHttpStatus().value(), errorCode.getMessage());
    }

    // Validation 검증 오류를 위한 코드
    public static ApiExceptionResponse of(int httpStatus, String message) {
        return new ApiExceptionResponse(false, httpStatus, message);
    }


}
