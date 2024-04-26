package com.hit.joonggonara.repository;

import com.hit.joonggonara.config.JPAConfig;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.type.LoginType;
import com.hit.joonggonara.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(locations = "classpath:/application.yaml")
@Import(JPAConfig.class)
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository sut;

    @Test
    @DisplayName("[JPA][QueryDsl] 회원 정보 존재")
    void existMemberTest() throws Exception
    {
        //given
        Member member = createMember();
        sut.save(member);
        //when
        boolean exceptedValue = sut.existByEmail(member.getEmail());
        //then
        assertThat(exceptedValue).isTrue();
    }

    @Test
    @DisplayName("[JPA][QueryDsl] 회원 정보 미 존재")
    void notExistMemberTest() throws Exception
    {
        //given
        Member member = createMember();
        //when
        boolean exceptedValue = sut.existByEmail(member.getEmail());
        //then
        assertThat(exceptedValue).isFalse();
    }

    private Member createMember() {
        return Member.builder()
                .email("test@naver.com")
                .name("name")
                .role(Role.USER)
                .loginType(LoginType.GENERAL)
                .build();
    }

}