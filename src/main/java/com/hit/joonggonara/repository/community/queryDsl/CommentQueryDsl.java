package com.hit.joonggonara.repository.community.queryDsl;

import com.hit.joonggonara.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentQueryDsl {

    Page<Comment> findCommentAllByCommunityId(Long communityId, Pageable pageable);
    Optional<Comment> findParentCommentById(Long id);
    List<Comment> findParentCommentAllByCommunityId(Long communityId);
}
