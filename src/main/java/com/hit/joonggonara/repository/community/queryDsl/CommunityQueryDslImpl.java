package com.hit.joonggonara.repository.community.queryDsl;

import com.hit.joonggonara.entity.Community;
import com.hit.joonggonara.entity.QCommunity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.hit.joonggonara.entity.QCommunity.community;

@RequiredArgsConstructor
public class CommunityQueryDslImpl implements CommunityQueryDsl {
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Optional<Community> findCommunityById(Long id) {
        Community community = jpaQueryFactory.selectFrom(QCommunity.community)
                .distinct()
                .leftJoin(QCommunity.community.comments).fetchJoin()
                .leftJoin(QCommunity.community.member).fetchJoin()
                .where(QCommunity.community.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(community);
    }

    @Override
    public Page<Community> findCommunityAll(Pageable pageable) {
        List<Community> communities = jpaQueryFactory.selectFrom(community)
                .distinct()
                .leftJoin(community.comments).fetchJoin()
                .leftJoin(community.member).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(community.createdDate.desc())
                .fetch();

        JPAQuery<Long> fetchQuery = jpaQueryFactory.select(community.id.count()).from(community);
        return PageableExecutionUtils.getPage(communities, pageable, fetchQuery::fetchOne);
    }

    @Override
    public Page<Community> findCommunitiesByKeyword(String keyword, Pageable pageable) {
        List<Community> communities = jpaQueryFactory.selectFrom(community)
                .distinct()
                .leftJoin(community.member).fetchJoin()
                .where(keywordContain(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(community.createdDate.desc())
                .fetch();

        JPAQuery<Long> fetchCount = jpaQueryFactory.select(community.id.count())
                .where(keywordContain(keyword));

        return PageableExecutionUtils.getPage(communities, pageable, fetchCount::fetchOne);
    }

    private BooleanExpression keywordContain(String keyword) {
        return keyword != null ? contentContain(keyword).or(nickNameContain(keyword)): null;

    }


    private BooleanExpression contentContain(String content) {
        return community.content.containsIgnoreCase(content);
    }

    private BooleanExpression nickNameContain(String nickName) {
        return community.member.nickName.containsIgnoreCase(nickName);
    }
}
