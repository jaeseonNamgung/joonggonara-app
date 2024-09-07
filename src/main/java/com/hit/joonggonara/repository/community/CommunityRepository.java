package com.hit.joonggonara.repository.community;

import com.hit.joonggonara.entity.Community;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {


    @EntityGraph(attributePaths = {"communityImages"})
    Optional<Community> findById(Long id);
}
