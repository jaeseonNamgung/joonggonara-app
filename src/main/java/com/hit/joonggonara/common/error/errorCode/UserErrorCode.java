package com.hit.joonggonara.common.error.errorCode;

import com.hit.joonggonara.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않은 회원입니다."),
    RECIPIENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),
    ACCESS_DENIED_ERROR(HttpStatus.FORBIDDEN, "해당 서비스에 대한 권한이 없습니다."),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "인증 오류입니다."),
    NOT_EXIST_AUTHORIZATION(HttpStatus.BAD_REQUEST, "권한이 존재하지 않습니다."),
    ALREADY_LOGGED_IN_USER(HttpStatus.BAD_REQUEST, "이미 로그인된 회원입니다."),
    ALREADY_LOGGED_OUT_USER(HttpStatus.BAD_REQUEST, "이미 로그아웃 된 회원입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "토큰 정보가 올바르지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "만료된 토큰 정보입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "지원되지 않는 토큰 정보입니다."),
    EXIST_USER_ID(HttpStatus.BAD_REQUEST, "이미 사용중인 아이디입니다."),
    EXIST_NICK_NAME(HttpStatus.BAD_REQUEST, "이미 사용중인 닉네임입니다."),
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "존재하지 않은 회원입니다."),
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "인증 코드가 일치 하지 않습니다." ),
    VERIFICATION_CODE_TIME_OVER(HttpStatus.BAD_REQUEST, "입력 시간을 초과했습니다. 인증 코드를 다시 요청해주세요."),
    NO_SUCH_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR,"보안 처리 중 오류가 발생했습니다. 잠시 후 다시 시도 바랍니다. (이 오류가 반복되면 관리자에게 문의 바랍니다.)"),
    NO_RANDOM_NUMBER(HttpStatus.INTERNAL_SERVER_ERROR, "보안 처리 중 오류가 발생했습니다. 잠시 후 다시 시도 바랍니다. (이 오류가 반복되면 관리자에게 문의 바랍니다.)"),
    NO_VERIFICATION_CODE(HttpStatus.INTERNAL_SERVER_ERROR, "인증 코드 처리 중 오류가 발생했습니다. 잠시 후 다시 시도 바랍니다. (이 오류가 반복되면 관리자에게 문의 바랍니다.)"),
    KID_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OIDC 요청에 거부되었습니다. 잠시 후 다시 시도 바랍니다."),
    REFRESH_TOKEN_EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "로그인 기간이 만료되었습니다. 다시 로그인해 주세요."),
    SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "메세지 전송 오류입니다.");


    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getName() {
        return name();
    }
}
