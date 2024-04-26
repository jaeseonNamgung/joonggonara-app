package com.hit.joonggonara.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequest(
        String email,
        @NotBlank(message = "인증 코드를 입력해주세요.")
        String verificationCode
) {
        public static EmailVerificationRequest of(String email, String verificationCode){
                return new EmailVerificationRequest(email, verificationCode);
        }
}
