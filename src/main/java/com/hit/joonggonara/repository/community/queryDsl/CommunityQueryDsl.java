package com.hit.joonggonara.repository.community.queryDsl;

import com.hit.joonggonara.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommunityQueryDsl {
    Optional<Community> findCommunityById(Long id);
    Page<Community> findCommunityAll(Pageable pageable);
    Page<Community> findCommunitiesByKeyword(String keyword, Pageable pageable);

}
