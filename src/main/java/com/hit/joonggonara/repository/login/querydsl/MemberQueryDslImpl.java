package com.hit.joonggonara.repository.login.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.hit.joonggonara.entity.QMember.member;

@RequiredArgsConstructor
public class MemberQueryDslImpl implements MemberQueryDsl{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existByEmail(String email) {
        Integer exist = jpaQueryFactory.selectOne()
                .from(member)
                .where(member.email.eq(email))
                .fetchFirst();
        return exist != null;
    }
}
