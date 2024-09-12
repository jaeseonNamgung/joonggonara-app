package com.hit.joonggonara.repository.community;

import com.hit.joonggonara.common.config.JPAConfig;
import com.hit.joonggonara.common.config.P6SpyConfig;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.entity.Community;
import com.hit.joonggonara.entity.Likes;
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

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:/application.yaml",properties = "spring.jpa.properties.hibernate.default_batch_fetch_size=100")
@Import({JPAConfig.class, P6SpyConfig.class})
@DataJpaTest
class LikesRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("[JPA][Delete] 커뮤니티 아이디와 화원 아이디로 좋아요 삭제")
    void deleteByMemberIdAndCommunityIdTest() throws Exception {
        //given
        Member member = createMember();
        entityManager.persist(member);
        Member savedMember = entityManager.find(Member.class, member.getId());
        Community community  = createCommunity(savedMember);
        entityManager.persist(community);
        Community savedCommunity = entityManager.find(Community.class, community.getId());
        Likes likes = Likes.builder().member(savedMember).community(savedCommunity).build();
        entityManager.persist(likes);
        //when

        likeRepository.deleteByMemberIdAndCommunityId(savedMember.getId(), savedCommunity.getId());
        entityManager.flush();
        entityManager.clear();
        //then
        Likes expectedLikes = entityManager.find(Likes.class, likes.getId());
        assertThat(expectedLikes).isNull();

    }

    private static Community createCommunity(Member member) {
        return Community.builder().content("community content").member(member).build();
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
