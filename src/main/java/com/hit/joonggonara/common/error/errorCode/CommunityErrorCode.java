package com.hit.joonggonara.common.error.errorCode;

import com.hit.joonggonara.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CommunityErrorCode implements ErrorCode {

    COMMUNITY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "게시글이 존재하지 않습니다.");



    private final HttpStatus httpStatus;
    private final String message;


    @Override
    public String getName() {
        return name();
    }
}
