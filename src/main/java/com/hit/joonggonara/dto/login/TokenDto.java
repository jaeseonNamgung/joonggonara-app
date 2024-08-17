package com.hit.joonggonara.dto.login;

public record TokenDto(
        String accessToken,
        String refreshToken,
        String principal
) {

    public static TokenDto of(
            String accessToken,
            String refreshToken,
            String principal
    ){
        return new TokenDto(accessToken, refreshToken, principal);
    }

}
