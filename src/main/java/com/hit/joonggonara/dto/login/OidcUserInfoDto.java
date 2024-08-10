package com.hit.joonggonara.dto.login;

import com.hit.joonggonara.common.type.LoginType;

public record OidcUserInfoDto(
        String email,
        LoginType loginType
) {

    public static OidcUserInfoDto of(
            String email
    ){
        return new OidcUserInfoDto(email, LoginType.KAKAO);
    }
}
