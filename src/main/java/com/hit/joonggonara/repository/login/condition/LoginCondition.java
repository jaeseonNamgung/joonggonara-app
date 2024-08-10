package com.hit.joonggonara.repository.login.condition;

import com.hit.joonggonara.common.type.LoginType;

public record LoginCondition(
        String principal,
        LoginType loginType
) {
    public static LoginCondition of(String principal, LoginType loginType){
        return new LoginCondition(principal, loginType);
    }
}
