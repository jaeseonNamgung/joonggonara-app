package com.hit.joonggonara.dto.request.login;

import jakarta.validation.constraints.NotBlank;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.NAME_NOT_BLANK;
import static com.hit.joonggonara.common.properties.ValidationMessageProperties.PHONE_NUMBER_NOT_BLANK;

public record FindUserIdBySmsRequest(
        @NotBlank(message = NAME_NOT_BLANK)
        String name,
        @NotBlank(message = PHONE_NUMBER_NOT_BLANK)
        String phoneNumber
) {

    public static FindUserIdBySmsRequest of(
            String name,
            String phoneNumber
    ){
        return new FindUserIdBySmsRequest(name, phoneNumber);
    }
}
