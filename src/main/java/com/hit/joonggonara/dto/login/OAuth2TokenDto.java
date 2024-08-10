package com.hit.joonggonara.dto.login;

public record OAuth2TokenDto(
        String access_token,
        String id_token

) {

    public static OAuth2TokenDto of(
            String accessToken,
            String idToken
    ) {
        return new OAuth2TokenDto(
                accessToken,
                idToken
        );
    }
}
