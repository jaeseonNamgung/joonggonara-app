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
        List<CommentResponse> childComments
) {
    public static CommentResponse of(Long id, String content, String nickName, String profile, Long parentId, String createdDate, List<CommentResponse> childComments) {
        return new CommentResponse(id, content, nickName, profile, parentId, createdDate, childComments);
    }
    public static CommentResponse fromResponse(Comment comment) {
        return CommentResponse.of(
                comment.getId(),
                comment.getContent(),
                comment.getMember().getNickName(),
                comment.getMember().getProfile(),
                comment.getParent()!= null ? comment.getParent().getId() : null,
                comment.getCreatedDate().toString(),
                comment.getChildren() != null ? fromResponse(comment.getChildren()) : new ArrayList<>()
                );
    }

    public static List<CommentResponse> fromResponse(List<Comment> comments) {
        return comments.stream().map(comment->
                CommentResponse.of(
                            comment.getId(),
                            comment.getContent(),
                            comment.getMember().getNickName(),
                            comment.getMember().getProfile(),
                            comment.getParent()!= null ? comment.getParent().getId() : null,
                            comment.getCreatedDate().toString(),
                            new ArrayList<>()
                    )
                ).toList();

    }

    public static Page<CommentResponse> fromResponse(Page<Comment> comments) {
        return comments.map(CommentResponse::fromResponse);
    }
}
