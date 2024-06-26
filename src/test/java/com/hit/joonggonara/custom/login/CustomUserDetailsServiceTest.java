package com.hit.joonggonara.custom.login;

import com.hit.joonggonara.common.custom.login.CustomUserDetailsService;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.repository.login.MemberRepository;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private CustomUserDetailsService sut;
    
    
    @Test
    @DisplayName("[Service] 회원 정보 존재")
    void userExistTest() throws Exception
    {
        //given
        Member member = createMember();
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
        //when
        UserDetails expectedUserDetails = sut.loadUserByUsername(member.getEmail());
        //then
        assertThat(expectedUserDetails.getUsername()).isEqualTo(member.getEmail());
        assertThat(expectedUserDetails.getPassword()).isEqualTo(member.getPassword());
        then(memberRepository).should().findByEmail(any());
    }
    
    @Test
    @DisplayName("[Service] 존재하지 않은 회원")
    void NotFoundUserTest() throws Exception
    {
        //given
        given(memberRepository.findByEmail(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException = (CustomException) catchRuntimeException(()-> sut.loadUserByUsername("test"));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.USER_NOT_FOUND.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        then(memberRepository).should().findByEmail(any());
    }

    private Member createMember() {
        return Member.builder()
                .email("test@naver.com")
                .name("홍길동")
                .password("abc1234")
                .nickName("nickName")
                .phoneNumber("010-1234-4567")
                .school("school")
                .loginType(LoginType.GENERAL)
                .role(Role.USER)
                .build();
    }

}