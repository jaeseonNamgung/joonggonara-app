package com.hit.joonggonara.repository.community;

import com.hit.joonggonara.entity.Community;
import com.hit.joonggonara.repository.community.queryDsl.CommunityQueryDsl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> , CommunityQueryDsl {

}
