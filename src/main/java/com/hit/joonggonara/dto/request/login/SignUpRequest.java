package com.hit.joonggonara.dto.request.login;

import com.hit.joonggonara.common.custom.validation.ValidationGroups.EmailGroup;
import com.hit.joonggonara.common.custom.validation.ValidationGroups.NotBlankGroup;
import com.hit.joonggonara.common.custom.validation.ValidationGroups.PasswordPatternGroup;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.*;

public record SignUpRequest(


        @NotBlank(message = USER_ID_NOT_BLANK, groups = NotBlankGroup.class)
        String userId,
        @NotBlank(message = EMAIL_NOT_BLANK, groups = NotBlankGroup.class)
        @Email(message = EMAIL,  groups = EmailGroup.class )
        String email,

        @NotBlank(message = PASSWORD_NOT_BLANK, groups = NotBlankGroup.class)
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
                message = PASSWORD_PATTERN, groups = PasswordPatternGroup.class)
        String password,

        @NotBlank(message = NAME_NOT_BLANK, groups = NotBlankGroup.class)
        String name,
        @NotBlank(message = NICK_NAME_NOT_BLANK, groups = NotBlankGroup.class)
        String nickName,

        @NotBlank(message = PHONE_NUMBER_NOT_BLANK, groups = NotBlankGroup.class)
        String phoneNumber,
        String loginType,
        Boolean isNotification
) {

    public static SignUpRequest of(
            String userId,
            String email,
            String password,
            String name,
            String nickName,
            String phoneNumber,
            String loginType,
            Boolean isNotification
    ){
        return new SignUpRequest(userId, email, password, name, nickName, phoneNumber, loginType,isNotification);
    }

    public Member toEntity(String passwordEncode){
        return Member.builder()
                .userId(userId)
                .email(email)
                .password(passwordEncode)
                .name(name)
                .nickName(nickName)
                .phoneNumber(phoneNumber)
                .profile(null)
                .isNotification(isNotification)
                .role(Role.ROLE_USER)
                .loginType(LoginType.checkType(loginType))
                .build();
    }




}
