package com.hit.joonggonara.dto.request.login;

import com.hit.joonggonara.common.custom.validation.ValidationGroups.EmailGroup;
import com.hit.joonggonara.common.custom.validation.ValidationGroups.NotBlankGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.*;

public record MemberUpdateRequest(

        @NotBlank(message = NICK_NAME_NOT_BLANK, groups = NotBlankGroup.class)
        String nickName,
        @Email(message = EMAIL, groups = EmailGroup.class)
        @NotBlank(message = EMAIL_NOT_BLANK, groups = NotBlankGroup.class)
        String email,
        @NotBlank(message = PHONE_NUMBER_NOT_BLANK, groups = NotBlankGroup.class)
        String phoneNumber,
        String profile,
        boolean isNotification
) {

        public static MemberUpdateRequest of(
                String nickName,
                String email,
                String phoneNumber,
                String profile,
                boolean isNotification
        ){
                return new MemberUpdateRequest(nickName, email, phoneNumber, profile, isNotification);
        }

}
