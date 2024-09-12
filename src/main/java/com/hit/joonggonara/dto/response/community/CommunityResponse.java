package com.hit.joonggonara.dto.response.community;

import com.hit.joonggonara.dto.response.product.PhotoResponse;
import com.hit.joonggonara.entity.Community;
import com.hit.joonggonara.entity.CommunityImage;
import org.springframework.data.domain.Page;

import java.util.List;

public record CommunityResponse(
        Long id,
        String content,
        Integer likeCount,
        Integer commentCount,
        String createdDate,
        String nickName,
        String profile,
        List<PhotoResponse> photos
) {
    public static CommunityResponse of(Long id, String content, Integer likeCount, Integer commentCount,
                                       String createdDate,String nickName,String profile, List<PhotoResponse> photos) {
        return new CommunityResponse(id, content, likeCount, commentCount,createdDate, nickName, profile,photos);
    }

    public static CommunityResponse fromResponse(Community community, List<CommunityImage> images) {
        return CommunityResponse.of(
                community.getId(),
                community.getContent(),
                community.getLikes().size(),
                community.getComments().size(),
                community.getCreatedDate().toString(),
                community.getMember().getNickName(),
                community.getMember().getProfile(),
                PhotoResponse.fromCommunityImageResponse(images)
        );
    }
    public static CommunityResponse fromResponse(Community community) {
        return CommunityResponse.of(
                community.getId(),
                community.getContent(),
                community.getLikes().size(),
                community.getComments().size(),
                community.getCreatedDate().toString(),
                community.getMember().getNickName(),
                community.getMember().getProfile(),
                community.getCommunityImages().isEmpty() ?
                        null : PhotoResponse.fromCommunityImageResponse(community.getCommunityImages())
        );
    }

    public static Page<CommunityResponse> fromResponse(Page<Community> communityPage) {
        return communityPage.map(community -> CommunityResponse.of(
                community.getId(),
                community.getContent(),
                community.getLikes().size(),
                community.getComments().size(),
                community.getCreatedDate().toString(),
                community.getMember().getNickName(),
                community.getMember().getProfile(),
                PhotoResponse.fromCommunityImageResponse(community.getCommunityImages())
        ));
    }
}
