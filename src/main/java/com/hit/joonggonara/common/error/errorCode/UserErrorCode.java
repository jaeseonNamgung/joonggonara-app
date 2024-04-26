package com.hit.joonggonara.common.error.errorCode;

import com.hit.joonggonara.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않은 회원입니다."),
    ACCESS_DENIED_ERROR(HttpStatus.FORBIDDEN, "해당 서비스에 대한 권한이 없습니다."),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "인증 오류입니다."),
    NOT_EXIST_AUTHORIZATION(HttpStatus.BAD_REQUEST, "권한이 존재하지 않습니다."),
    ALREADY_LOGGED_IN_USER(HttpStatus.BAD_REQUEST, "이미 로그인된 회원입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "토큰 정보가 올바르지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "만료된 토큰 정보입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "지원되지 않는 토큰 정보입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 오류입니다. 잠시후 다시 시도 바랍니다. (이 오류가 반복되면 관리자에게 문의 바랍니다.)"),
    NO_SUCH_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR,"보안 처리 중 오류가 발생했습니다. 잠시후 다시 시도 바랍니다. (이 오류가 반복되면 관리자에게 문의 바랍니다.)"),
    NO_RANDOM_NUMBER(HttpStatus.INTERNAL_SERVER_ERROR, "보안 처리 중 오류가 발생했습니다. 잠시후 다시 시도 바랍니다. (이 오류가 반복되면 관리자에게 문의 바랍니다.)"),
    NO_VERIFICATION_CODE(HttpStatus.INTERNAL_SERVER_ERROR, "인증 코드 처리 중 오류가 발생했습니다. 잠시후 다시 시도 바랍니다. (이 오류가 반복되면 관리자에게 문의 바랍니다.)");

    private final HttpStatus httpStatus;
    private final String message;
}
