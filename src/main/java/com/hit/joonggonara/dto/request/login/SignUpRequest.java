package com.hit.joonggonara.dto.request.login;

import com.hit.joonggonara.common.custom.validation.ValidationGroups.EmailGroup;
import com.hit.joonggonara.common.custom.validation.ValidationGroups.NotBlankGroup;
import com.hit.joonggonara.common.custom.validation.ValidationGroups.PasswordPatternGroup;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(

        @NotBlank(message = "이메일을 입력해주세요", groups = NotBlankGroup.class)
        @Email(message = "이메일 주소를 정확히 입력해주세요.",  groups = EmailGroup.class )
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요", groups = NotBlankGroup.class)
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
                message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용해 주세요.", groups = PasswordPatternGroup.class)
        String password,

        @NotBlank(message = "이름을 입력해주세요")
        String name,
        @NotBlank(message = "닉네임을 입력해주세요")
        String nickName,

        @NotBlank(message = "학교를 입력해주세요")
        String school,

        @NotBlank(message = "전화번호를 입력해주세요", groups = NotBlankGroup.class)
        String phoneNumber
) {

    public static SignUpRequest of(
            String email,
            String password,
            String name,
            String nickName,
            String school,
            String phoneNumber
    ){
        return new SignUpRequest(email, password, name, nickName, school, phoneNumber);
    }

    public Member toEntity(String passwordEncode){
        return Member.builder()
                .email(email)
                .password(passwordEncode)
                .name(name)
                .nickName(nickName)
                .school(school)
                .phoneNumber(phoneNumber)
                .role(Role.USER)
                .loginType(LoginType.GENERAL)
                .build();
    }




}
