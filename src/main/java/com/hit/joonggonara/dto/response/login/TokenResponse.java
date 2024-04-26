package com.hit.joonggonara.dto.response;

import com.hit.joonggonara.dto.TokenDto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
    public static TokenResponse of(String accessToken, String refreshToken){
        return new TokenResponse(accessToken, refreshToken);
    }

    public static TokenResponse ToResponse(TokenDto tokenDto){
        return TokenResponse.of(tokenDto.accessToken(), tokenDto.refreshToken());
    }
}
