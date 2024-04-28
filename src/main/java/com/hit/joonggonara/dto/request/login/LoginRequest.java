package com.hit.joonggonara.dto.request.login;

import jakarta.validation.constraints.NotBlank;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.ID_PASSWORD_NOT_BLANK;

public record LoginRequest(

        @NotBlank(message = ID_PASSWORD_NOT_BLANK)
        String userId,
        @NotBlank(message = ID_PASSWORD_NOT_BLANK)
        String password
) {
    public static LoginRequest of(String userId, String password){
        return new LoginRequest(userId, password);
    }
}
