package com.hit.joonggonara.service.user;


import com.hit.joonggonara.dto.request.login.SignUpRequest;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.repository.login.MemberRepository;
import com.hit.joonggonara.service.login.SignUpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {
    
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private SignUpService sut;
    
    @Test
    @DisplayName("[Service] 회원가입 테스트")
    void signUpSuccessTest() throws Exception
    {
        //given
        SignUpRequest signUpRequest = createSignUpRequest();
        given(memberRepository.existByEmail(any())).willReturn(false);
        given(memberRepository.save(any())).willReturn(any());
        //when
        boolean isTrue = sut.signUp(signUpRequest);
        //then
        assertThat(isTrue).isTrue();

        then(memberRepository).should().existByEmail(any());
        then(memberRepository).should().save(any());
    }

    @Test
    @DisplayName("[Service] 회원가입 중 이미 가입되어 있는 회원 테스트")
    void signUpAlReadyExistUserTest() throws Exception
    {
        //given
        SignUpRequest signUpRequest = createSignUpRequest();
        given(memberRepository.existByEmail(any())).willReturn(true);
        //when
        CustomException expectedException =
                (CustomException) catchRuntimeException(() -> sut.signUp(signUpRequest));

        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.ALREADY_LOGGED_IN_USER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.ALREADY_LOGGED_IN_USER.getMessage());

        then(memberRepository).should().existByEmail(any());

    }

    private SignUpRequest createSignUpRequest() {
        return SignUpRequest.of(
                "email@naver.com",
                "Abc123456*",
                "hong",
                "nickName",
                "hit",
                "010-1234-1234"
        );
    }

}