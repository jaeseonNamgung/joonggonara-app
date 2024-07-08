package com.hit.joonggonara.service.login;


import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.dto.request.login.SignUpPhoneNumberRequest;
import com.hit.joonggonara.dto.request.login.SignUpRequest;
import com.hit.joonggonara.dto.request.login.VerificationRequest;
import com.hit.joonggonara.repository.login.MemberRepository;
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
    private VerificationService verificationService;
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private SignUpService sut;
    
    @Test
    @DisplayName("[Service] 회원가입 성공 시 true을 리턴")
    void signUpSuccessTest() throws Exception
    {
        //given
        SignUpRequest signUpRequest = createSignUpRequest();
        given(memberRepository.existByUserId(any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn("123456");
        given(memberRepository.save(any())).willReturn(any());
        //when
        boolean isTrue = sut.signUp(signUpRequest);
        //then
        assertThat(isTrue).isTrue();

        then(memberRepository).should().existByUserId(any());
        then(passwordEncoder).should().encode(any());
        then(memberRepository).should().save(any());
    }

    @Test
    @DisplayName("[Service] 회원가입 중 이미 가입되어 있는 회원이면 ALREADY_LOGGED_IN_USER 에러 발생")
    void signUpAlReadyExistUserTest() throws Exception
    {
        //given
        SignUpRequest signUpRequest = createSignUpRequest();
        given(memberRepository.existByUserId(any())).willReturn(true);
        //when
        CustomException expectedException =
                (CustomException) catchRuntimeException(() -> sut.signUp(signUpRequest));

        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.ALREADY_LOGGED_IN_USER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.ALREADY_LOGGED_IN_USER.getMessage());

        then(memberRepository).should().existByUserId(any());

    }
    
    @Test
    @DisplayName("[Service] 아이디 중복 검사 성공 시 true을 리턴")
    void checkDuplicateUserIdTest() throws Exception
    {
        //given
        String userId = "testId";
        given(memberRepository.existByUserId(any())).willReturn(false);
        //when
        boolean expectedValue = sut.checkDuplicateUserId(userId);
        //then
        assertThat(expectedValue).isTrue();

        then(memberRepository).should().existByUserId(any());
    }
    
    @Test
    @DisplayName("[Service] 아이디 중복 검사 실패 시 EXIST_USER_ID 에러 발생")
    void existUserIdTest() throws Exception
    {
        //given
        String userId = "testId";
        given(memberRepository.existByUserId(any())).willReturn(true);
        //when
        CustomException expectedException =
                (CustomException) catchRuntimeException(() -> sut.checkDuplicateUserId(userId));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.EXIST_USER_ID.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.EXIST_USER_ID.getMessage());

        then(memberRepository).should().existByUserId(any());
    }
    
    @Test
    @DisplayName("[Service][SMS] 핸드폰 인증 코드가 정상적으로 발송되고 레디스에 값이 저장 될 경우 true를 리턴")
    void sendSuccessSmsAndSaveVerificationCodeInRedisTest() throws Exception
    {
        //given
        SignUpPhoneNumberRequest phoneNumberRequest = SignUpPhoneNumberRequest.of("+8612345678");
        //when
        boolean expectedValue = sut.sendSmsVerificationCode(phoneNumberRequest);
        //then
        assertThat(expectedValue).isTrue();
        then(verificationService).should().sendSms(any());
    }
    @Test
    @DisplayName("[Service][인증 검사] 인증 코드가 일치할 경우 true를 리턴")
    void verificationCodeMatchTest() throws Exception
    {
        //given
        VerificationRequest verificationRequest = createVerificationRequest();
        given(verificationService.checkVerificationCode(any(), any())).willReturn(true);
        //when
        boolean expectedValue = sut.checkCode(verificationRequest);
        //then
        assertThat(expectedValue).isTrue();
        then(verificationService).should().checkVerificationCode(any(), any());
    }

    @Test
    @DisplayName("[Service][인증 검사] 인증 코드가 일치하지 않을 경우 false를 리턴")
    void verificationCodeNotMatchTest() throws Exception
    {
        //given
        VerificationRequest verificationRequest = createVerificationRequest();
        given(verificationService.checkVerificationCode(any(), any())).willReturn(false);
        //when
        boolean expectedValue = sut.checkCode(verificationRequest);
        //then
        assertThat(expectedValue).isFalse();
        then(verificationService).should().checkVerificationCode(any(), any());
    }

    private VerificationRequest createVerificationRequest() {
        return VerificationRequest.of("+8612345678", "123456");
    }

    private SignUpRequest createSignUpRequest() {
        return SignUpRequest.of(
                "principal@naver.com",
                "Abc123456*",
                "hong",
                "nickName",
                "hit",
                "010-1234-1234",
                "general",
                true
        );
    }

}