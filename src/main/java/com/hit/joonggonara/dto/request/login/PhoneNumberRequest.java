package com.hit.joonggonara.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PhoneNumberRequest(

        @NotBlank(message = "전화번호를 입력해주세요.")
        String phoneNumber
) {

    public static PhoneNumberRequest of(
            String phoneNumber
    ){
        return new PhoneNumberRequest(phoneNumber);
    }
}
