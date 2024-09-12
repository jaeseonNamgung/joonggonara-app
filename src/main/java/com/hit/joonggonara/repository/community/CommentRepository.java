package com.hit.joonggonara.repository.community;

import com.hit.joonggonara.entity.Comment;
import com.hit.joonggonara.repository.community.queryDsl.CommentQueryDsl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryDsl {
}
