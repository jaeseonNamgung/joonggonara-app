package com.hit.joonggonara.repository.login.querydsl;

import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.VerificationType;
import com.hit.joonggonara.repository.login.condition.AuthenticationCondition;
import com.hit.joonggonara.repository.login.condition.VerificationCondition;

import java.util.Optional;

public interface MemberQueryDsl{

    boolean existByUserId(String userId);
    boolean existByEmail(String email);
    boolean existByEmailAndLoginType(String email, LoginType loginType);
    boolean existByUserNameAndVerificationTypeValue(
            VerificationCondition condition, VerificationType verificationType
    );

    Optional<String> findUserIdOrPasswordByPhoneNumberOrEmail(AuthenticationCondition condition);
}
