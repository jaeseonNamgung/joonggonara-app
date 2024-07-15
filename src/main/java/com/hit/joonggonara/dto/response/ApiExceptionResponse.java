package com.hit.joonggonara.dto.response;

import com.hit.joonggonara.common.error.ErrorCode;

public record ApiExceptionResponse(
        boolean success,
        int httpStatus,
        String errorType,
        String message
) {

    public static ApiExceptionResponse of(ErrorCode errorCode) {
        return new ApiExceptionResponse(false, errorCode.getHttpStatus().value(), errorCode.getName(), errorCode.getMessage());
    }

}
