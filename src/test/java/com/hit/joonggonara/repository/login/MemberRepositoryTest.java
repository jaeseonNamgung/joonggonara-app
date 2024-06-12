package com.hit.joonggonara.repository.login;

import com.hit.joonggonara.common.config.JPAConfig;
import com.hit.joonggonara.common.config.P6SpyConfig;
import com.hit.joonggonara.common.type.AuthenticationType;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.common.type.VerificationType;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.repository.login.condition.AuthenticationCondition;
import com.hit.joonggonara.repository.login.condition.LoginCondition;
import com.hit.joonggonara.repository.login.condition.VerificationCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(locations = "classpath:/application.yaml")
@Import({JPAConfig.class, P6SpyConfig.class})
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
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        sut.save(member);
        //when
        boolean exceptedValue = sut.existByEmail(member.getEmail());
        //then
        assertThat(exceptedValue).isTrue();
    }

    @Test
    @DisplayName("[JPA][QueryDsl] 아이디로 검색해서 회원이 없을 경우 false를 리턴")
    void UserNotExistByUserIdTest() throws Exception
    {
        //given
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        //when
        boolean exceptedValue = sut.existByUserId(member.getUserId());
        //then
        assertThat(exceptedValue).isFalse();
    }

    @Test
    @DisplayName("[JPA][QueryDsl] 이메일로 검색해서 회원이 없을 경우 false를 리턴")
    void UserNotExistByEmailTest() throws Exception
    {
        //given
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        //when
        boolean exceptedValue = sut.existByEmail(member.getEmail());
        //then
        assertThat(exceptedValue).isFalse();
    }
    @Test
    @DisplayName("[JPA][QueryDsl]  이메일로 검색해서 회원이 있을 경우 true를 리턴")
    void UserExistByEmailTest() throws Exception
    {
        //given
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        sut.save(member);
        //when
        boolean exceptedValue = sut.existByEmail(member.getEmail());
        //then
        assertThat(exceptedValue).isTrue();
    }



    @Test
    @DisplayName("[JPA][QueryDsl]  이메일과 로그인 타입(카카오)으로 검색해서 회원이 있을 경우 true를 리턴")
    void UserExistByEmailAndLoginTest() throws Exception
    {
        //given
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        sut.save(member);
        //when
        boolean exceptedValue = sut.existByEmailAndLoginType(member.getEmail(), member.getLoginType());
        //then
        assertThat(exceptedValue).isTrue();
    }

    @Test
    @DisplayName("[JPA][QueryDsl] 이메일과 로그인 타입(카카오)으로 검색해서 회원이 없을 경우 false를 리턴")
    void UserNotExistByEmailAndLoginTypeTest() throws Exception
    {
        //given
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        //when
        boolean exceptedValue = sut.existByEmailAndLoginType(member.getUserId(), member.getLoginType());
        //then
        assertThat(exceptedValue).isFalse();
    }
    @Test
    @DisplayName("[JPA][QueryDsl][아이디 찾기] 이름과 전화번호로 검색해서 회원이 있을 경우 true를 리턴")
    void UserExistByUserNameAndPhoneNumberTest() throws Exception
    {
        //given
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
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

    @Test
    @DisplayName("[JPA][SoftDelete] Delete 쿼리 시 Update 쿼리 실행 is_delete가 false일 경우만 조회 ")
    void deleteByUserIdTest() throws Exception
    {
        //given
<<<<<<< Updated upstream
        Member member1 = createMember("testId1", LoginType.KAKAO);
        Member member2 = createMember("testId2", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member1 = createMember("testId1");
        Member member2 = createMember("testId2");
=======
<<<<<<< Updated upstream
        Member member1 = createMember("testId1", LoginType.KAKAO);
        Member member2 = createMember("testId2", LoginType.KAKAO);
=======
        Member member1 = createMember("testId1", LoginType.GENERAL);
        Member member2 = createMember("testId2", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes

        sut.save(member1);
        sut.save(member2);
        //when
        sut.deleteByUserId(member1.getUserId());
        List<Member> expectedMembers = sut.findAll();
        //then
        assertThat(expectedMembers).isNotNull();
        assertThat(expectedMembers.size()).isEqualTo(1);
        assertThat(expectedMembers.get(0).isDeleted()).isFalse();
    }
    
    @Test
    @DisplayName("[JPA][QueryDsl] Email 찾기를 통해 회원 아이디를 Optional로 리턴")
    void ReturnUserIdToOptionalByEmail() throws Exception
    {
        //given
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        AuthenticationCondition authenticationCondition =
                AuthenticationCondition.of("test@email.com", VerificationType.EMAIL, AuthenticationType.ID);
        sut.save(member);
        //when
        String expectedUserId = sut.findUserIdOrPasswordByPhoneNumberOrEmail(authenticationCondition).get();
        //then
        assertThat(expectedUserId).isEqualTo(member.getUserId());
    }
    @Test
    @DisplayName("[JPA][QueryDsl] Sms 찾기를 통해 회원 아이디를 Optional로 리턴")
    void ReturnUserIdToOptionalBySMS() throws Exception
    {
        //given
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        AuthenticationCondition authenticationCondition =
                AuthenticationCondition.of("+8612345678", VerificationType.SMS, AuthenticationType.ID);
        sut.save(member);
        //when
        String expectedUserId = sut.findUserIdOrPasswordByPhoneNumberOrEmail(authenticationCondition).get();
        //then
        assertThat(expectedUserId).isEqualTo(member.getUserId());
    }

    @Test
    @DisplayName("[JPA][QueryDsl] Email 찾기를 통해 패스워드를 Optional로 리턴")
    void ReturnPasswordToOptionalByEmail() throws Exception
    {
        //given
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        AuthenticationCondition authenticationCondition =
                AuthenticationCondition.of("test@email.com", VerificationType.EMAIL, AuthenticationType.PASSWORD);
        sut.save(member);
        //when
        String expectedPassword = sut.findUserIdOrPasswordByPhoneNumberOrEmail(authenticationCondition).get();
        //then
        assertThat(expectedPassword).isEqualTo(member.getPassword());
    }

    @Test
    @DisplayName("[JPA][QueryDsl] Sms 찾기를 통해 패스워드를 Optional로 리턴")
    void ReturnPasswordToOptionalBySMS() throws Exception
    {
        //given
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
<<<<<<< Updated upstream
        Member member = createMember("testId");
=======
<<<<<<< Updated upstream
        Member member = createMember("testId", LoginType.KAKAO);
=======
        Member member = createMember("testId", LoginType.GENERAL);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        AuthenticationCondition authenticationCondition =
                AuthenticationCondition.of("+8612345678", VerificationType.SMS, AuthenticationType.PASSWORD);
        sut.save(member);
        //when
        String expectedPassword = sut.findUserIdOrPasswordByPhoneNumberOrEmail(authenticationCondition).get();
        //then
        assertThat(expectedPassword).isEqualTo(member.getPassword());
    }
    
    @Test
    @DisplayName("[JPA][QueryDsl] 일반 로그인 회원 일 경우 userId 를 통해 조회 후 Optional 객체를 반환")
    void returnOptionalMemberWhenUserIsAGeneralUser() throws Exception
    {
        //given
        LoginCondition loginCondition =  LoginCondition.of("testId", LoginType.GENERAL);
        Member member = createMember("testId", LoginType.GENERAL);
        sut.save(member);
        //when
        Member expectedMember = sut.findByPrincipal(loginCondition).get();
        //then
        assertThat(expectedMember).isNotNull();
        assertThat(expectedMember.getUserId()).isEqualTo(loginCondition.principal());
        assertThat(expectedMember.getLoginType()).isEqualTo(loginCondition.loginType());
    }

    @Test
    @DisplayName("[JPA][QueryDsl] 일반 로그인 회원 일 경우 userId 를 통해 조회 후 존재하지 않은 회원 일 경우 Optional에 빈 객체를 반환")
    void returnOptionalEmptyWhenGeneralUserIsNotExist() throws Exception
    {
        //given
        LoginCondition loginCondition = LoginCondition.of("testId", LoginType.GENERAL);
        //when
        Optional<Member> expectedMember = sut.findByPrincipal(loginCondition);
        //then
        assertThat(expectedMember).isEmpty();
    }

    @Test
    @DisplayName("[JPA][QueryDsl] 소셜 로그인 회원 일 경우 email 를 통해 조회 후 Optional 객체를 반환")
    void returnOptionalMemberWhenUserIsSocialUser() throws Exception
    {
        //given
        LoginCondition loginCondition = LoginCondition.of("test@email.com", LoginType.KAKAO);
        Member member = createMember("test@email.com", LoginType.KAKAO);
        sut.save(member);
        //when
        Member expectedMember = sut.findByPrincipal(loginCondition).get();
        //then
        assertThat(expectedMember).isNotNull();
        assertThat(expectedMember.getUserId()).isEqualTo(loginCondition.principal());
        assertThat(expectedMember.getLoginType()).isEqualTo(loginCondition.loginType());
    }


    @Test
    @DisplayName("[JPA][QueryDsl] 존재하는 닉네임일 경우 true를 반환")
    void returnTrueIfExistNickName() throws Exception
    {
        //given
        Member member = createMember("testId", LoginType.KAKAO);
        sut.save(member);
        //when
        boolean expectedTrue = sut.existByNickName(member.getNickName());
        //then
        assertThat(expectedTrue).isTrue();
    }
    @Test
    @DisplayName("[JPA][QueryDsl] 존재하지 않는 닉네임일 경우 false를 반환")
    void returnFalseIfNotExistNickName() throws Exception
    {
        //given
        Member member = createMember("testId", LoginType.KAKAO);
        sut.save(member);
        //when
        boolean expectedTrue = sut.existByNickName("FalseNickName");
        //then
        assertThat(expectedTrue).isFalse();
    }
    
    @Test
    @DisplayName("[JPA][QueryDsl] 일반 회원일 경우 아이디로 회원 조회")
    void checkMemberWithUserIDIfMemberIsAGeneralMember() throws Exception
    {
        //given
        String principal = "userId";
        Member member = createMember("userId", LoginType.GENERAL);
        Member savedMember = sut.save(member);
        //when
        Member expectedMember = sut.findByPrincipalAndLoginType(principal, LoginType.GENERAL).get();
        //then
        assertThat(expectedMember.getId()).isEqualTo(savedMember.getId());
        assertThat(expectedMember.getUserId()).isEqualTo(savedMember.getUserId());
        assertThat(expectedMember.getLoginType()).isEqualTo(savedMember.getLoginType());
    }

    @Test
    @DisplayName("[JPA][QueryDsl] 소셜 로그인 회원일 경우 이메일로 회원 조회")
    void checkMemberWithUserIDIfMemberIsSocialLoginMember() throws Exception
    {
        //given
        String principal = "test@email.com";
        Member member = createMember("userId", LoginType.KAKAO);
        Member savedMember = sut.save(member);
        //when
        Member expectedMember = sut.findByPrincipalAndLoginType(principal, LoginType.KAKAO).get();
        //then
        assertThat(expectedMember.getId()).isEqualTo(savedMember.getId());
        assertThat(expectedMember.getEmail()).isEqualTo(savedMember.getEmail());
        assertThat(expectedMember.getLoginType()).isEqualTo(savedMember.getLoginType());
    }


    private Member createMember(String userId, LoginType loginType) {
        return Member.builder()
                .userId(userId)
                .email("test@email.com")
                .name("hong")
                .nickName("nickName")
                .password("Abc1234*")
                .phoneNumber("+8612345678")
                .role(Role.ROLE_USER)
                .loginType(loginType)
                .build();
    }

}