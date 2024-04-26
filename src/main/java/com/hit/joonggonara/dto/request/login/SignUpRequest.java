package com.hit.joonggonara.dto.request;

import com.hit.joonggonara.custom.validation.ValidationGroups.EmailGroup;
import com.hit.joonggonara.custom.validation.ValidationGroups.NotBlankGroup;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.type.LoginType;
import com.hit.joonggonara.type.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(

        @NotBlank(message = "이메일을 입력해주세요", groups = NotBlankGroup.class)
        @Email(message = "이메일 주소를 정확히 입력해주세요.",  groups = EmailGroup.class )
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요")
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
