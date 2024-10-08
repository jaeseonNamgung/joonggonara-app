package com.hit.joonggonara.repository.login.querydsl;

import com.hit.joonggonara.common.type.AuthenticationType;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.VerificationType;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.repository.login.condition.AuthenticationCondition;
import com.hit.joonggonara.repository.login.condition.LoginCondition;
import com.hit.joonggonara.repository.login.condition.VerificationCondition;
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
                .where(member.userId.eq(userId), member.deleted.isFalse())
                .fetchFirst();
        return isExist != null;
    }

    @Override
    public boolean existByNickName(String nickName) {
        Integer isExist = jpaQueryFactory.selectOne()
                .from(member)
                .where(member.nickName.eq(nickName), member.deleted.isFalse())
                .fetchFirst();
        return isExist != null;
    }
    @Override
    public boolean existByEmail(String email) {
        Integer isExist = jpaQueryFactory.selectOne()
                .from(member)
                .where(member.email.eq(email), member.deleted.isFalse())
                .fetchFirst();
        return isExist != null;
    }

    @Override
    public boolean existByEmailAndLoginType(String email, LoginType loginType) {
        Integer isExist = jpaQueryFactory.selectOne()
                .from(member)
                .where(member.email.eq(email).and(member.loginType.eq(loginType))
                        , member.deleted.isFalse())
                .fetchOne();
        return isExist != null;
    }

    @Override
    public boolean existByUserNameAndVerificationTypeValue(VerificationCondition condition, VerificationType verificationType) {
        Integer exist = jpaQueryFactory.selectOne()
                .from(member)
                .where(member.name.eq(condition.username())
                        ,verificationType(condition, verificationType)
                        ,member.deleted.isFalse()
                        ,member.loginType.eq(LoginType.GENERAL)
                )
                .fetchOne();
        return exist != null;
    }


    @Override
    public Optional<String> findUserIdOrPasswordByPhoneNumberOrEmail(AuthenticationCondition condition) {
        String authentication = jpaQueryFactory.select(checkIdOfPassword(condition.authenticationType()))
                .from(member)
                .where(authenticationCondition(condition), member.deleted.isFalse(), member.loginType.eq(LoginType.GENERAL))
                .fetchFirst();

        return Optional.ofNullable(authentication);
    }
    @Override
    public Optional<Member> findByPrincipalAndLoginType(LoginCondition condition) {

        Member fetchMember = jpaQueryFactory.selectFrom(member)
                .where(
                        loginCondition(condition),member.loginType.eq(condition.loginType()),
                        member.deleted.isFalse())
                .fetchOne();
        return Optional.ofNullable(fetchMember);
    }

    @Override
    public Optional<Member> withDrawlFindByPrincipal(LoginCondition condition) {
        Member fetchOne = jpaQueryFactory.selectFrom(member)
                .distinct()
                .leftJoin(member.products).fetchJoin()
                .where(loginCondition(condition),
                        member.loginType.eq(condition.loginType()),
                        member.deleted.isFalse())
                .fetchOne();
        return Optional.ofNullable(fetchOne);
    }

    private BooleanExpression loginCondition(LoginCondition condition) {
        if (condition.loginType().equals(LoginType.GENERAL)){
            return member.userId.eq(condition.principal());
        }else{
            return member.email.eq(condition.principal());
        }
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
