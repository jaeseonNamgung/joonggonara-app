package com.hit.joonggonara.dto.request;

import com.hit.joonggonara.common.custom.validation.ValidationGroups.EmailGroup;
import com.hit.joonggonara.common.custom.validation.ValidationGroups.NotBlankGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(


        @NotBlank(message = "이메일 또는 비밀번호를 입력해주세요.", groups = NotBlankGroup.class)
        @Email(message = "이메일 주소를 정확히 입력해주세요.", groups = EmailGroup.class)
        String email,
        @NotBlank(message = "이메일 또는 비밀번호를 입력해주세요.", groups = NotBlankGroup.class)
        String password
) {
    public static LoginRequest of(String email, String password){
        return new LoginRequest(email, password);
    }
}
