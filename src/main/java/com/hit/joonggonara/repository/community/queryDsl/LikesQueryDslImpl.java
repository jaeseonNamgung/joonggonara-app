package com.hit.joonggonara.repository.community.queryDsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.hit.joonggonara.entity.QLikes.likes;

@RequiredArgsConstructor
public class LikesQueryDslImpl implements  LikesQueryDsl {

    private final JPAQueryFactory queryFactory;


    @Override
    public boolean existsByMemberIdAndCommunityId(Long memberId, Long communityId) {
        Integer existLikes = queryFactory.selectOne()
                .from(likes)
                .where(likes.member.id.eq(memberId), likes.community.id.eq(communityId))
                .fetchOne();
        return existLikes != null;
    }
}
