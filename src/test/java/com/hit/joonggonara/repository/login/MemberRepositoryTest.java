package com.hit.joonggonara.repository.login;

import com.hit.joonggonara.common.config.JPAConfig;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.common.type.VerificationType;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.repository.login.condition.VerificationCondition;
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
class 
MemberRepositoryTest {

    @Autowired
    private MemberRepository sut;

    @Test
    @DisplayName("[JPA][QueryDsl]  아이디로 검색해서 회원이 있을 경우 true를 리턴")
    void UserExistByUserIdTest() throws Exception
    {
        //given
        Member member = createMember();
        sut.save(member);
        //when
        boolean exceptedValue = sut.existByUserId(member.getUserId());
        //then
        assertThat(exceptedValue).isTrue();
    }

    @Test
    @DisplayName("[JPA][QueryDsl] 아이디로 검색해서 회원이 없을 경우 false를 리턴")
    void UserNotExistByUserIdTest() throws Exception
    {
        //given
        Member member = createMember();
        //when
        boolean exceptedValue = sut.existByUserId(member.getUserId());
        //then
        assertThat(exceptedValue).isFalse();
    }
    
    @Test
    @DisplayName("[JPA][QueryDsl][아이디 찾기] 이름과 전화번호로 검색해서 회원이 있을 경우 true를 리턴")
    void UserExistByUserNameAndPhoneNumberTest() throws Exception
    {
        //given
        Member member = createMember();
        VerificationCondition condition = VerificationCondition.of("hong", "+8612345678");
        sut.save(member);
        //when
        boolean expectedValue =
                sut.existByUserNameAndVerificationTypeValue(
                        condition,
                        VerificationType.ID_SMS
                );
        //then
        assertThat(expectedValue).isTrue();
    }
    @Test
    @DisplayName("[JPA][QueryDsl][아이디 찾기] 이름과 전화번호로 검색해서 회원이 없을 경우 false를 리턴")
    void UserNotExistByUserNameAndPhoneNumberTest() throws Exception
    {
        //given
        VerificationCondition condition = VerificationCondition.of("hong", "+8612345678");
        //when
        boolean expectedValue =
                sut.existByUserNameAndVerificationTypeValue(
                        condition,
                        VerificationType.ID_SMS);
        //then
        assertThat(expectedValue).isFalse();
    }
    
    @Test
    @DisplayName("[JPA][QueryDsl][아이디 찾기] 이름과 이메일로 검색해서 회원이 있을 경우 true를 리턴")
    void UserExistByUserNameAndEmailTest() throws Exception
    {
        //given
        Member member = createMember();
        VerificationCondition condition = VerificationCondition.of("hong", "test@email.com");
        sut.save(member);
        //when
        boolean expectedValue = sut.existByUserNameAndVerificationTypeValue(
                condition,
                VerificationType.ID_EMAIL);
        //then
        assertThat(expectedValue).isTrue();
    }
    @Test
    @DisplayName("[JPA][QueryDsl][아이디 찾기] 이름과 이메일로 검색해서 회원이 없을 경우 false를 리턴")
    void UserNotExistByUserNameAndEmailTest() throws Exception
    {
        //given
        VerificationCondition condition = VerificationCondition.of("hong", "test@email.com");
        //when
        boolean expectedValue = sut.existByUserNameAndVerificationTypeValue(
                condition,
                VerificationType.ID_EMAIL);
        //then
        assertThat(expectedValue).isFalse();
    }


    @Test
    @DisplayName("[JPA][QueryDsl][비밀번호 찾기] 이름과 전화번호로 검색해서 회원이 있을 경우 true를 리턴")
    void UserExistByUserNameAndUserIdAndPhoneNumberTest() throws Exception
    {
        //given
        Member member = createMember();
        VerificationCondition condition = VerificationCondition.of("hong", "testId", "+8612345678");
        sut.save(member);
        //when
        boolean expectedValue =
                sut.existByUserNameAndVerificationTypeValue(
                        condition,
                        VerificationType.PASSWORD_SMS
                );
        //then
        assertThat(expectedValue).isTrue();
    }
    @Test
    @DisplayName("[JPA][QueryDsl][비밀번호 찾기] 이름과 전화번호로 검색해서 회원이 없을 경우 false를 리턴")
    void UserNotExistByUserNameAndUserIdAndPhoneNumberTest() throws Exception
    {
        //given
        VerificationCondition condition = VerificationCondition.of("hong","testId", "+8612345678");
        //when
        boolean expectedValue =
                sut.existByUserNameAndVerificationTypeValue(
                        condition,
                        VerificationType.PASSWORD_EMAIL);
        //then
        assertThat(expectedValue).isFalse();
    }

    @Test
    @DisplayName("[JPA][QueryDsl][비밀번호 찾기] 이름과 아이디, 이메일로 검색해서 회원이 있을 경우 true를 리턴")
    void UserExistByUserNameAndUserIdAndEmailTest() throws Exception
    {
        //given
        Member member = createMember();
        VerificationCondition condition = VerificationCondition.of("hong", "testId", "test@email.com");
        sut.save(member);
        //when
        boolean expectedValue = sut.existByUserNameAndVerificationTypeValue(
                condition,
                VerificationType.PASSWORD_EMAIL);
        //then
        assertThat(expectedValue).isTrue();
    }
    @Test
    @DisplayName("[JPA][QueryDsl][아이디 찾기] 이름과 이메일로 검색해서 회원이 없을 경우 false를 리턴")
    void UserNotExistByUserNameAndUserIdAndEmailTest() throws Exception
    {
        //given
        VerificationCondition condition = VerificationCondition.of("hong","testId", "test@email.com");
        //when
        boolean expectedValue = sut.existByUserNameAndVerificationTypeValue(
                condition,
                VerificationType.PASSWORD_EMAIL);
        //then
        assertThat(expectedValue).isFalse();
    }

    private Member createMember() {
        return Member.builder()
                .userId("testId")
                .email("test@email.com")
                .name("hong")
                .phoneNumber("+8612345678")
                .role(Role.USER)
                .loginType(LoginType.GENERAL)
                .build();
    }

}