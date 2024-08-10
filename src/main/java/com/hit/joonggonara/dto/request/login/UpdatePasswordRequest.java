package com.hit.joonggonara.dto.request.login;

import jakarta.validation.constraints.NotBlank;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.PASSWORD_NOT_BLANK;
import static com.hit.joonggonara.common.properties.ValidationMessageProperties.USER_ID_NOT_BLANK;

public record UpdatePasswordRequest(

        @NotBlank(message = USER_ID_NOT_BLANK)
        String userId,
        @NotBlank(message = PASSWORD_NOT_BLANK)
        String password
) {

    public static UpdatePasswordRequest of(String userId, String password) {
        return new UpdatePasswordRequest(userId, password);
    }
}
