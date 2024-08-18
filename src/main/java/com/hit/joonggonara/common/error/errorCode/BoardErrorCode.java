package com.hit.joonggonara.common.error.errorCode;

import com.hit.joonggonara.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum BoardErrorCode implements ErrorCode {

    NOT_UPLOADED_IMAGE(HttpStatus.BAD_REQUEST, "이미지가 업로드 되지 않았습니다."),
    MISMATCH_EXTENSION(HttpStatus.BAD_REQUEST, "png 또는 jpg 확장자만 업로드 가능합니다." ),
    IO_ERROR(HttpStatus.BAD_REQUEST, "이미지가 존재하지 않거나 이미지에 접근할 수 없습니다." );


    private final HttpStatus httpStatus;
    private final String message;


    @Override
    public String getName() {
        return name();
    }
}
