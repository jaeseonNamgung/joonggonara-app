package com.hit.joonggonara.repository.community.queryDsl;

public interface LikesQueryDsl {
    boolean existsByMemberIdAndCommunityId(Long memberId, Long communityId);
}
