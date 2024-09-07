package com.hit.joonggonara.dto.response.community;

import com.hit.joonggonara.dto.response.product.PhotoResponse;
import com.hit.joonggonara.entity.Community;
import com.hit.joonggonara.entity.CommunityImage;

import java.util.List;

public record CommunityResponse(
        Long id,
        String content,
        Integer likeCount,
        List<PhotoResponse> photos
) {
    public static CommunityResponse of(Long id, String content, Integer likeCount, List<PhotoResponse> photos) {
        return new CommunityResponse(id, content, likeCount, photos);
    }

    public static CommunityResponse fromResponse(Community community, List<CommunityImage> images) {
        return CommunityResponse.of(
                community.getId(),
                community.getContent(),
                community.getLikeCount(),
                PhotoResponse.fromCommunityImageResponse(images)
        );
    }
    public static CommunityResponse fromResponse(Community community) {
        return CommunityResponse.of(
                community.getId(),
                community.getContent(),
                community.getLikeCount(),
                List.of()
        );
    }
}
