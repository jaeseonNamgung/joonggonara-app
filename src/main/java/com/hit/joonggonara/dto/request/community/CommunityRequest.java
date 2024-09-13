package com.hit.joonggonara.dto.request.community;

import com.hit.joonggonara.entity.Community;
import com.hit.joonggonara.entity.Member;
import jakarta.validation.constraints.NotBlank;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.CONTENT_NOT_BLANK;

public record CommunityRequest(
        @NotBlank(message = CONTENT_NOT_BLANK)
        String content
) {
    public static CommunityRequest of(String content) {
        return new CommunityRequest(content);
    }

    public Community toEntity(Member member){
        return Community.builder().content(content).member(member).build();
    }
}
