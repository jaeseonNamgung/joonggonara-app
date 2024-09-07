package com.hit.joonggonara.repository.community;

import com.hit.joonggonara.common.config.JPAConfig;
import com.hit.joonggonara.common.config.P6SpyConfig;
import com.hit.joonggonara.entity.Comment;
import com.hit.joonggonara.entity.Community;
import com.hit.joonggonara.entity.CommunityImage;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:/application.yaml")
@Import({JPAConfig.class, P6SpyConfig.class})
@DataJpaTest
class CommunityRepositoryTest {

    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private CommunityImageRepository communityImageRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private EntityManager entityManager;


    @Test
    @DisplayName("[JPA] findById @EntityGraph 테스트")
    void findByIdTest() throws Exception {
        //given
        Community community = Community.builder().content("content").build();
        Community savedCommunity = communityRepository.save(community);

        CommunityImage communityImage = CommunityImage.builder()
                .filePath("filePath").fileName("fileName").community(savedCommunity).build();

        communityImageRepository.save(communityImage);
        entityManager.flush();
        entityManager.clear();
        //when
        Community expectedCommunity = communityRepository.findById(savedCommunity.getId()).get();

        //then
        assertThat(expectedCommunity.getContent()).isEqualTo("content");
        assertThat(expectedCommunity.getCommunityImages().size()).isEqualTo(1);


    }
    @Test
    @DisplayName("[JPA] 커뮤니티와 연관된 엔티티 삭제 테스트")
    void deleteCommunityTest() throws Exception {
        //given
        Community community = Community.builder().content("content").build();
        Community savedCommunity = communityRepository.save(community);

        CommunityImage communityImage = CommunityImage.builder()
                .filePath("filePath").fileName("fileName").community(savedCommunity).build();

        Comment comment = Comment.builder().content("comment").community(savedCommunity).build();

        CommunityImage savedImage = communityImageRepository.save(communityImage);
        Comment savedComment = commentRepository.save(comment);
        entityManager.flush();
        entityManager.clear();
        //when
        communityRepository.deleteById(savedCommunity.getId());
        entityManager.flush();
        entityManager.clear();
        Optional<Comment> commentOptional = commentRepository.findById(savedComment.getId());
        Optional<CommunityImage> imageOptional= communityImageRepository.findById(savedImage.getId());
        //then
        assertThat(commentOptional.isEmpty()).isTrue();
        assertThat(imageOptional.isEmpty()).isTrue();


    }

}
