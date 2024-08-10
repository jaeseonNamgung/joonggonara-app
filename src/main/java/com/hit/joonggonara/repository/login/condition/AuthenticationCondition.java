package com.hit.joonggonara.repository.login.condition;

import com.hit.joonggonara.common.type.AuthenticationType;
import com.hit.joonggonara.common.type.VerificationType;

public record AuthenticationCondition(

        // PhoneNumber of Email
        String verificationKey,
        VerificationType verificationType,
        AuthenticationType authenticationType

) {

    public static AuthenticationCondition of(
            String email,
            VerificationType verificationType,
            AuthenticationType authenticationType
            ){
        return new AuthenticationCondition(email, verificationType, authenticationType);
    }



}
