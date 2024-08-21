package com.hit.joonggonara.dto.response.login;


import com.hit.joonggonara.dto.login.TokenDto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
    public static TokenResponse of(String accessToken, String refreshToken){
        return new TokenResponse(accessToken, refreshToken);
    }

    public static TokenResponse toResponse(TokenDto tokenDto){
        return TokenResponse.of(
                tokenDto.accessToken(),
                tokenDto.refreshToken());
    }
}
