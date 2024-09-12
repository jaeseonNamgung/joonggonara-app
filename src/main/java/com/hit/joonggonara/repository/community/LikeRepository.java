package com.hit.joonggonara.repository.community;

import com.hit.joonggonara.entity.Likes;
import com.hit.joonggonara.repository.community.queryDsl.LikesQueryDsl;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LikeRepository extends JpaRepository<Likes, Long>, LikesQueryDsl {

    @Modifying
    @Query("delete from Likes l where l.community.id = :communityId and l.member.id = :memberId ")
    void deleteByMemberIdAndCommunityId(@Param("memberId") Long memberId, @Param("communityId") Long communityId);


}
