package com.hit.joonggonara.dto;

public record TokenDto(
        String accessToken,
        String refreshToken,
        String email
) {

    public static TokenDto of(
            String accessToken,
            String refreshToken,
            String email
    ){
        return new TokenDto(accessToken, refreshToken, email);
    }

}
