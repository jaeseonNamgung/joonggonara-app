package com.hit.joonggonara.repository.community.queryDsl;

import com.hit.joonggonara.entity.Comment;
import com.hit.joonggonara.entity.QComment;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.hit.joonggonara.entity.QComment.comment;

@RequiredArgsConstructor
public class CommentQueryDslImpl implements CommentQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Comment> findCommentAllByCommunityId(Long communityId, Pageable pageable) {
        List<Comment> Comments = queryFactory.selectFrom(comment)
                .distinct()
                .leftJoin(comment.member).fetchJoin()
                .leftJoin(comment.children).fetchJoin()
                .where(comment.community.id.eq(communityId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(comment.createdDate.desc())
                .fetch();

        JPAQuery<Long> count = queryFactory.select(comment.id.count())
                .from(comment)
                .where(comment.community.id.eq(communityId));

        return PageableExecutionUtils.getPage(Comments, pageable, count::fetchOne);
    }

    // parent 가 null 이면 최상위 부모
    @Override
    public Optional<Comment> findParentCommentById(Long id) {
        Comment comment = queryFactory.selectFrom(QComment.comment)
                .leftJoin(QComment.comment.member).fetchJoin()
                .leftJoin(QComment.comment.children).fetchJoin()
                .where(QComment.comment.id.eq(id), QComment.comment.parent.isNull())
                .fetchOne();
        return Optional.ofNullable(comment);
    }

    @Override
    public List<Comment> findParentCommentAllByCommunityId(Long communityId) {
        return queryFactory.selectFrom(comment)
                .leftJoin(comment.member).fetchJoin()
                .leftJoin(comment.children).fetchJoin()
                .where(comment.community.id.eq(communityId), comment.parent.isNull())
                .orderBy(comment.createdDate.desc())
                .fetch();
    }
}
