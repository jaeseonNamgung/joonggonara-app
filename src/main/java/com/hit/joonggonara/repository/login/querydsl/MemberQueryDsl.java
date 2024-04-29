package com.hit.joonggonara.repository.login.querydsl;

import com.hit.joonggonara.common.type.VerificationType;
import com.hit.joonggonara.repository.login.condition.VerificationCondition;

public interface MemberQueryDsl{

    boolean existByUserId(String userId);
    boolean existByUserNameAndVerificationTypeValue(
            VerificationCondition condition, VerificationType verificationType
    );

}
