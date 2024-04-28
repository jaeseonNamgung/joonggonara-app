package com.hit.joonggonara.dto.request.login;

import jakarta.validation.constraints.NotBlank;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.*;

public record FindPasswordBySmsRequest(

        @NotBlank(message = NAME_NOT_BLANK)
        String name,

        @NotBlank(message = USER_ID_NOT_BLANK)
        String userId,

        @NotBlank(message = PHONE_NUMBER_NOT_BLANK)
        String phoneNumber
) {
    public static FindPasswordBySmsRequest of(
            String name,
            String userId,
            String phoneNumber
    ){
        return new FindPasswordBySmsRequest (name, userId, phoneNumber);
    }
}
