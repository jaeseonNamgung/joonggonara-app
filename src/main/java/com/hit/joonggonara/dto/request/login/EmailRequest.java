package com.hit.joonggonara.dto.request;

import com.hit.joonggonara.custom.validation.ValidationGroups;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @NotBlank(message = "이메일을 입력해주세요.", groups = ValidationGroups.NotBlankGroup.class)
        @Email(message = "이메일 주소를 정확히 입력해주세요.", groups = ValidationGroups.EmailGroup.class)
        String email
) {
    public static EmailRequest of(String email){
        return new EmailRequest(email);
    }
}
