package com.hit.joonggonara.dto.request.community;

import com.hit.joonggonara.entity.Comment;
import com.hit.joonggonara.entity.Community;
import com.hit.joonggonara.entity.Member;
import jakarta.validation.constraints.NotBlank;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.CONTENT_NOT_BLANK;

public record CommentRequest(

        Long commentId,
        @NotBlank(message = CONTENT_NOT_BLANK)
        String content
) {
    public static CommentRequest of(Long commentId, String content) {
        return new CommentRequest(commentId, content);
    }
    public Comment toEntity(Community community, Member member){
        return Comment.builder().content(content).community(community).member(member).build();
    }
}
