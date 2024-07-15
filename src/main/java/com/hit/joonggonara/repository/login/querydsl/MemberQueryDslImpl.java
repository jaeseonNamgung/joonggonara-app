package com.hit.joonggonara.repository.login.querydsl;

import com.hit.joonggonara.common.type.AuthenticationType;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.VerificationType;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.repository.login.condition.AuthenticationCondition;
import com.hit.joonggonara.repository.login.condition.VerificationCondition;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.hit.joonggonara.entity.QMember.member;

@RequiredArgsConstructor
public class MemberQueryDslImpl implements MemberQueryDsl{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existByUserId(String userId) {
        Integer isExist = jpaQueryFactory.selectOne()
                .from(member)
                .where(member.userId.eq(userId))
                .fetchFirst();
        return isExist != null;
    }

    @Override
    public boolean existByEmail(String email) {
        Integer isExist = jpaQueryFactory.selectOne()
                .from(member)
                .where(member.email.eq(email))
                .fetchFirst();
        return isExist != null;
    }

    @Override
    public boolean existByEmailAndLoginType(String email, LoginType loginType) {
        Integer isExist = jpaQueryFactory.selectOne()
                .from(member)
                .where(member.email.eq(email).and(member.loginType.eq(loginType)))
                .fetchOne();
        return isExist != null;
    }

    @Override
    public boolean existByUserNameAndVerificationTypeValue(VerificationCondition condition, VerificationType verificationType) {
        Integer exist = jpaQueryFactory.selectOne()
                .from(member)
                .where(member.name.eq(condition.username()), verificationType(condition, verificationType))
                .fetchOne();
        return exist != null;
    }

    @Override
    public boolean existByNickName(String nickName) {
        Integer exist = jpaQueryFactory.selectOne()
                .from(member)
                .where(member.nickName.eq(nickName))
                .fetchOne();
        return exist != null;
    }

    @Override
    public Optional<String> findUserIdOrPasswordByPhoneNumberOrEmail(AuthenticationCondition condition) {
        String authentication = jpaQueryFactory.select(checkIdOfPassword(condition.authenticationType()))
                .from(member)
                .where(authenticationCondition(condition))
                .fetchFirst();

        return Optional.ofNullable(authentication);
    }
    @Override
    public Optional<Member> findByPrincipalAndLoginType(String principal, LoginType loginType) {
        Member fetchMember = jpaQueryFactory.selectFrom(member)
                .where(principalAndLoginTypeCondition(principal, loginType))
                .fetchFirst();
        return Optional.of(fetchMember);
    }

    private BooleanExpression principalAndLoginTypeCondition(String principal, LoginType loginType) {
        if(LoginType.GENERAL.equals(loginType)){
            return member.userId.eq(principal);
        }
        return member.email.eq(principal);
    }

    private BooleanExpression verificationType(VerificationCondition condition, VerificationType verificationType) {

        if(VerificationType.ID_SMS.equals(verificationType)){
            return member.phoneNumber.eq(condition.verificationCode());
        } else if(VerificationType.ID_EMAIL.equals(verificationType)){
            return member.email.eq(condition.verificationCode());
        } else if(VerificationType.PASSWORD_SMS.equals(verificationType)){
            return member.phoneNumber.eq(condition.verificationCode()).and(member.userId.eq(condition.userId()));
        } else{
            return member.email.eq(condition.verificationCode()).and(member.userId.eq(condition.userId()));
        }
    }

    private BooleanExpression authenticationCondition(AuthenticationCondition condition) {
        if(VerificationType.EMAIL.equals(condition.verificationType())){
            return member.email.eq(condition.verificationKey());
        }else{
            return member.phoneNumber.eq(condition.verificationKey());
        }
    }

    private StringPath checkIdOfPassword(AuthenticationType authenticationType){
        if(AuthenticationType.ID.equals(authenticationType)){
            return member.userId;
        }else{
            return member.password;
        }

    }



}
