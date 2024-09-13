package com.hit.joonggonara.dto.request.login;

import com.hit.joonggonara.common.custom.validation.ValidationGroups;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.entity.Member;
import jakarta.validation.constraints.NotBlank;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.*;

public record SocialSignUpRequest(

        String email,
        String profile,
        @NotBlank(message = NAME_NOT_BLANK, groups = ValidationGroups.NotBlankGroup.class)
        String name,
        @NotBlank(message = NICK_NAME_NOT_BLANK, groups = ValidationGroups.NotBlankGroup.class)
        String nickName,

        @NotBlank(message = PHONE_NUMBER_NOT_BLANK, groups = ValidationGroups.NotBlankGroup.class)
        String phoneNumber,
        String loginType,
        Boolean isNotification
) {
    public Member toEntity() {
        return Member.builder()
                .userId("")
                .email(email)
                .password("")
                .name(name)
                .nickName(nickName)
                .phoneNumber(phoneNumber)
                .profile(profile)
                .isNotification(isNotification)
                .role(Role.ROLE_USER)
                .loginType(LoginType.checkType(loginType))
                .build();
    }
}
