package com.hit.joonggonara.repository.login.querydsl;

import com.hit.joonggonara.common.type.VerificationType;
import com.hit.joonggonara.service.login.condition.VerificationCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.hit.joonggonara.entity.QMember.member;

@RequiredArgsConstructor
public class MemberQueryDslImpl implements MemberQueryDsl{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existByUserId(String userId) {
        Integer exist = jpaQueryFactory.selectOne()
                .from(member)
                .where(member.userId.eq(userId))
                .fetchFirst();
        return exist != null;
    }

    @Override
    public boolean existByUserNameAndVerificationTypeValue(VerificationCondition condition, VerificationType verificationType) {
        Integer exist = jpaQueryFactory.selectOne()
                .from(member)
                .where(member.name.eq(condition.username()), verificationType(condition, verificationType))
                .fetchOne();
        return exist != null;
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

}
