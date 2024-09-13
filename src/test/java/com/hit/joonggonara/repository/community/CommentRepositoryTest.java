package com.hit.joonggonara.repository.community;

import com.hit.joonggonara.common.config.JPAConfig;
import com.hit.joonggonara.common.config.P6SpyConfig;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.entity.Comment;
import com.hit.joonggonara.entity.Community;
import com.hit.joonggonara.entity.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:/application.yaml",properties = "spring.jpa.properties.hibernate.default_batch_fetch_size=100")
@Import({JPAConfig.class, P6SpyConfig.class})
@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private EntityManager entityManager;



    @Test
    @DisplayName("[QueryDsl] 부모 댓글만 전체 조회")
    void findParentCommentAllByCommunityIdTest() throws Exception {
        //given
        Member member = createMember();
        entityManager.persist(member);
        Member savedMember = entityManager.find(Member.class, member.getId());

        Community community = Community.builder()
                .content("community content")
                .member(savedMember)
                .build();
        entityManager.persist(community);
        Community savedCommunity = entityManager.find(Community.class, community.getId());

        for (int i = 1; i <= 10 ; i++) {
            Comment comment = Comment.builder()
                    .member(savedMember)
                    .community(savedCommunity)
                    .content("comment content"+i)
                    .build();
            Comment savedComment = commentRepository.save(comment);
            if(i % 2 == 0) {
                Comment childComment = Comment.builder()
                        .member(savedMember)
                        .community(savedCommunity)
                        .content("child comment content"+i)
                        .build();
                childComment.addParentComment(savedComment);
                commentRepository.save(childComment);
            }
        }

        //when
        List<Comment> expectedComment = commentRepository.findParentCommentAllByCommunityId(savedCommunity.getId());

        //then
        assertThat(expectedComment).hasSize(10);
        assertThat(expectedComment.get(0).getParent()).isNull();
        assertThat(expectedComment.get(0).getChildren().get(0).getContent()).isEqualTo("child comment content10");
        assertThat(expectedComment.get(1).getChildren()).isEmpty();

    }

    private Member createMember() {
        return Member.builder()
                .userId("userId")
                .email("test@email.com")
                .name("hong")
                .nickName("nickName")
                .password("Abc1234*")
                .phoneNumber("+8612345678")
                .role(Role.ROLE_USER)
                .loginType(LoginType.GENERAL)
                .build();
    }
}
