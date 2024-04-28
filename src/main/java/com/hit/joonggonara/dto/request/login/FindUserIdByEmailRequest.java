package com.hit.joonggonara.dto.request.login;

import com.hit.joonggonara.common.custom.validation.ValidationGroups;
import com.hit.joonggonara.common.custom.validation.ValidationGroups.EmailGroup;
import com.hit.joonggonara.common.custom.validation.ValidationGroups.NotBlankGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.*;

public record FindUserIdByEmailRequest(
        @NotBlank(message = NAME_NOT_BLANK, groups = NotBlankGroup.class)
        String name,
        @NotBlank(message = EMAIL_NOT_BLANK, groups = NotBlankGroup.class)
        @Email(message = EMAIL, groups = EmailGroup.class)
        String email
) {
    public static FindUserIdByEmailRequest of(String name, String email){
        return new FindUserIdByEmailRequest(name, email);
    }
}
