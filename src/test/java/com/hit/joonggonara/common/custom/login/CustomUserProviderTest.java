package com.hit.joonggonara.common.custom.login;


import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
class CustomUserProviderTest {

    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomUserProvider sut;


    @Test
    @DisplayName("사용자 로그인 인증 성공 테스트")
    void userProviderSuccessTest() throws Exception
    {
        //given
        String userId = "testId";
        String password = "abc1234";
        Authentication authentication = new UsernamePasswordAuthenticationToken(userId, password);
        CustomUserDetails userDetails = createUserDetails();
        given(userDetailsService.loadUserByUsername(any()))
                .willReturn(userDetails);
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        //when
        Authentication expectedAuthentication = sut.authenticate(authentication);
        //then

        assertThat(expectedAuthentication.getPrincipal()).isEqualTo(userDetails.getUsername());
        assertThat(expectedAuthentication.getCredentials()).isEqualTo(userDetails.getPassword());
        String role = expectedAuthentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().get();
        assertThat(role).isEqualTo(Role.ROLE_USER.name());


        then(userDetailsService).should().loadUserByUsername(any());
        then(passwordEncoder).should().matches(any(), any());
    }

    @Test
    @DisplayName("비밀번호 불 일치 테스트")
    void passwordMismatchTest() throws Exception
    {
        //given
        String userId = "testId";
        String password = "abc1234";
        Authentication authentication = new UsernamePasswordAuthenticationToken(userId, password);
        CustomUserDetails userDetails = createUserDetails();
        given(userDetailsService.loadUserByUsername(any()))
                .willReturn(userDetails);
        given(passwordEncoder.matches(any(), any())).willReturn(false);
        //when
        CustomException expectedException =
                (CustomException) catchRuntimeException(() -> sut.authenticate(authentication));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.USER_NOT_FOUND.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        then(userDetailsService).should().loadUserByUsername(any());
        then(passwordEncoder).should().matches(any(), any());
    }

    private CustomUserDetails createUserDetails() {
        return new CustomUserDetails(createMember());
    }

    private Member createMember() {
        return Member.builder()
                .userId("testId")
                .password("abc1234")
                .role(Role.ROLE_USER)
                .build();
    }

}