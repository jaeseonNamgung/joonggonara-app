package com.hit.joonggonara.dto.response.community;

import com.hit.joonggonara.entity.Comment;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public record CommentResponse(
        Long id,
        String content,
        String nickName,
        String profile,
        Long parentId,
        String createdDate,
        boolean isDeleted,
        List<CommentResponse> childComments
) {
    public static CommentResponse of(Long id, String content, String nickName, String profile, Long parentId, String createdDate, boolean isDeleted, List<CommentResponse> childComments) {
        return new CommentResponse(id, content, nickName, profile, parentId, createdDate, isDeleted,childComments);
    }
    public static CommentResponse fromResponse(Comment comment) {
        return CommentResponse.of(
                comment.getId(),
                comment.getContent(),
                comment.getMember().getNickName(),
                comment.getMember().getProfile(),
                comment.getParent()!= null ? comment.getParent().getId() : null,
                comment.getCreatedDate() !=null ? comment.getCreatedDate().toString() : null,
                comment.getIsDeleted(),
                comment.getChildren() != null ? fromResponse(comment.getChildren()) : new ArrayList<>()
                );
    }

    public static List<CommentResponse> fromResponse(List<Comment> comments) {
        return comments.stream().map(CommentResponse::fromResponse).toList();

    }

    public static Page<CommentResponse> fromResponse(Page<Comment> comments) {
        return comments.map(CommentResponse::fromResponse);
    }
}
