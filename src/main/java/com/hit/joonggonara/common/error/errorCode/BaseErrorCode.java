package com.hit.joonggonara.common.error.errorCode;

import com.hit.joonggonara.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum BaseErrorCode implements ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 오류입니다. 잠시후 다시 시도 바랍니다. (이 오류가 반복되면 관리자에게 문의 바랍니다.)");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getName() {
        return name();
    }
}
