package com.hit.joonggonara.repository.community;

import com.hit.joonggonara.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
