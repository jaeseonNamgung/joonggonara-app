package com.hit.joonggonara.dto.request.community;

import com.hit.joonggonara.entity.Community;
import jakarta.validation.constraints.NotBlank;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.CONTENT_NOT_BLANK;

public record CommunityRequest(
        @NotBlank(message = CONTENT_NOT_BLANK)
        String content
) {
    public static CommunityRequest of(String content) {
        return new CommunityRequest(content);
    }

    public Community toEntity(){
        return Community.builder().content(content).build();
    }
}
