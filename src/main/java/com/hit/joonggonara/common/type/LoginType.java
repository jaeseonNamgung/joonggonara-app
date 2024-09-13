package com.hit.joonggonara.common.type;

import lombok.Getter;

@Getter
public enum LoginType {

    GENERAL, NAVER, GOGGLE, KAKAO;

    public static LoginType checkType(String loginType) {
        loginType = loginType.toUpperCase();
        return switch (loginType) {
            case "GOOGLE" -> LoginType.GOGGLE;
            case "NAVER" -> LoginType.NAVER;
            case "KAKAO" -> LoginType.KAKAO;
            default -> LoginType.GENERAL;
        };
    }
}
