package com.hit.joonggonara.repository.login.querydsl;

import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.VerificationType;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.repository.login.condition.AuthenticationCondition;
import com.hit.joonggonara.repository.login.condition.LoginCondition;
import com.hit.joonggonara.repository.login.condition.VerificationCondition;

import java.util.Optional;

public interface MemberQueryDsl{

    boolean existByUserId(String userId);
    boolean existByNickName(String nickName);
    boolean existByEmail(String email);
    boolean existByEmailAndLoginType(String email, LoginType loginType);
    boolean existByUserNameAndVerificationTypeValue(
            VerificationCondition condition, VerificationType verificationType
    );

<<<<<<< Updated upstream
    boolean existByNickName(String nickName);

    Optional<String> findUserIdOrPasswordByPhoneNumberOrEmail(AuthenticationCondition condition);
    Optional<Member> findByPrincipalAndLoginType(String principal, LoginType loginType);
=======
<<<<<<< Updated upstream
    Optional<String> findUserIdOrPasswordByPhoneNumberOrEmail(AuthenticationCondition condition);
=======
<<<<<<< Updated upstream
    boolean existByNickName(String nickName);

    Optional<String> findUserIdOrPasswordByPhoneNumberOrEmail(AuthenticationCondition condition);
    Optional<Member> findByPrincipalAndLoginType(String principal, LoginType loginType);
=======

    Optional<String> findUserIdOrPasswordByPhoneNumberOrEmail(AuthenticationCondition condition);

    Optional<Member> findByPrincipal(LoginCondition condition);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
}
