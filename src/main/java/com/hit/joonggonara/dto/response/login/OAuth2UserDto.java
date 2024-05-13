package com.hit.joonggonara.dto.response.login;

import com.hit.joonggonara.dto.login.TokenDto;

public record OAuth2UserDto(
        String accessToken,
        String refreshToken,
        String principal,
        // 회원가입 -> true, 회원가입 X -> false
        Boolean signUpStatus
) {
    public static OAuth2UserDto of(
            String accessToken,
            String refreshToken,
            String principal,
            Boolean signUpStatus
    ){
        return new OAuth2UserDto(accessToken, refreshToken, principal, signUpStatus);
    }

    public static OAuth2UserDto fromOAuth2UserDto(TokenDto tokenDto){
        return OAuth2UserDto.of(tokenDto.accessToken(), tokenDto.refreshToken(), tokenDto.principal(), true);
    }
    public static OAuth2UserDto fromOAuth2UserDto(String principal){
        return OAuth2UserDto.of(null, null, principal, false);
    }

}
