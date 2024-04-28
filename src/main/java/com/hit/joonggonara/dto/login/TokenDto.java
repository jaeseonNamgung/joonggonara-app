package com.hit.joonggonara.dto.login;

public record TokenDto(
        String accessToken,
        String refreshToken,
        String userId
) {

    public static TokenDto of(
            String accessToken,
            String refreshToken,
            String userId
    ){
        return new TokenDto(accessToken, refreshToken, userId);
    }

}
