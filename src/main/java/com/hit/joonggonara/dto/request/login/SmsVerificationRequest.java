package com.hit.joonggonara.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SmsVerificationRequest(

        String phoneNumber,

        @NotBlank(message = "인증 코드를 입력해주세요.")
        String verificationCode
) {

        public static SmsVerificationRequest of(
                String phoneNumber,
                String verificationCode
        ){
                return new SmsVerificationRequest(phoneNumber, verificationCode);
        }
}
