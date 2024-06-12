package com.hit.joonggonara.dto.response.login;

import com.hit.joonggonara.dto.login.TokenDto;

public record OAuth2UserDto(
        String accessToken,
        String refreshToken,
        String principal,
        String profile,
        // 회원가입 -> true, 회원가입 X -> false
        Boolean signUpStatus
) {
    public static OAuth2UserDto of(
            String accessToken,
            String refreshToken,
            String principal,
            String profile,
            Boolean signUpStatus
    ){
        return new OAuth2UserDto(accessToken, refreshToken, principal,profile, signUpStatus);
    }

    public static OAuth2UserDto fromOAuth2UserDto(TokenDto tokenDto, String profile){
        return OAuth2UserDto.of(tokenDto.accessToken(), tokenDto.refreshToken(), tokenDto.principal(), profile,true);
    }
    public static OAuth2UserDto fromOAuth2UserDto(String principal, String profile){
        return OAuth2UserDto.of(null, null, principal, profile,false);
    }

}
