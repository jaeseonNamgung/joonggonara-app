package com.hit.joonggonara.dto.request.login;

import jakarta.validation.constraints.NotBlank;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.VERIFICATION_CODE_NOT_BLANK;

public record VerificationRequest(

        String verificationKey,

        @NotBlank(message = VERIFICATION_CODE_NOT_BLANK)
        String verificationCode
) {

        public static VerificationRequest of(
                String verificationKey,
                String verificationCode
        ){
                return new VerificationRequest(verificationKey, verificationCode);
        }
}
