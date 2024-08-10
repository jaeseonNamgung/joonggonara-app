package com.hit.joonggonara.service.login;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.util.EmailUtil;
import com.hit.joonggonara.common.util.RedisUtil;
import com.hit.joonggonara.common.util.CoolSmsUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    private CoolSmsUtil coolSmsUtil;
    @Mock
    private EmailUtil emailUtil;
    @Mock
    private RedisUtil redisUtil;
    @InjectMocks
    private VerificationService sut;


    @Test
    @DisplayName("[Service] 정확한 인증 코드 일 때 true를 리턴")
    void smsVerificationCodeSuccessTest() throws Exception
    {
        //given
        String verificationCode = "123456";
        String key = "test key";
        given(redisUtil.get(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.checkVerificationCode(key, verificationCode);
        //then
        assertThat(expectedValue).isTrue();

        then(redisUtil).should().get(any());

    }
    @Test
    @DisplayName("[Service] 인증 코드가 다를 때 false를 리턴")
    void differentSmsVerificationCodeTest() throws Exception
    {
        //given
        String verificationCode = "123456";
        String key = "test key";
        given(redisUtil.get(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.checkVerificationCode(key, "456789");

        //then
        assertThat(expectedValue).isFalse();

        then(redisUtil).should().get(any());

    }

    @Test
    @DisplayName("[Service] 레디스에 인증 코드가 null 일경우 NO_VERIFICATION_CODE 에러 발생")
    void noSmsVerificationCodeTest() throws Exception
    {
        //given
        String verificationCode = "123456";
        String key = "test key";
        given(redisUtil.get(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException = (CustomException) catchException(() ->
                sut.checkVerificationCode(key, verificationCode));

        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.VERIFICATION_CODE_TIME_OVER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.VERIFICATION_CODE_TIME_OVER.getMessage());

        then(redisUtil).should().get(any());
    }


    @Test
    @DisplayName("[Service][Sms] 인증 코드가 정상적으로 발급 되고 레디스에 저장되었을 때 true을 리턴")
    void sendVerificationCodeSuccessfulAndSaveInRedisTest() throws Exception
    {
        //given
        String phoneNumber = "+8612345678";
        given(coolSmsUtil.sendMessage(any())).willReturn(Optional.of("123456"));

        //when
        sut.sendSms(phoneNumber);
        //then
        then(coolSmsUtil).should().sendMessage(any());
        then(redisUtil).should().removeAndSave(any(), any(), any());
    }

    @Test
    @DisplayName("[Service][Sms] 인증 코드가 정상적으로 발급이 되지 않을 때 NO_VERIFICATION_CODE 에러 발생")
    void NoVerificationCodeExceptionTest() throws Exception
    {
        //given
        String sms = "+8612345678";
        given(coolSmsUtil.sendMessage(any())).willReturn(Optional.empty());

        //when
        CustomException expectedException =
                (CustomException)catchException(()->sut.sendSms(sms));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.NO_VERIFICATION_CODE.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NO_VERIFICATION_CODE.getMessage());

        then(coolSmsUtil).should().sendMessage(any());
    }

    @Test
    @DisplayName("[Service][Email] 회원 정보가 존재하고  이메일 인증코드가 정상적으로 발송 및 Redis에 저장 될 경우 true를 리턴")
    void findUserIdByEmailSuccessTest() throws Exception
    {
        //given
        String verificationCode = "123456";
        String email = "test@principal.com";
        given(emailUtil.createMessage(any())).willReturn(Optional.of(verificationCode));
        //when
        sut.sendEmail(email);
        //then

        then(emailUtil).should().createMessage(any());
        then(redisUtil).should().removeAndSave(any(), any(), any());
    }

    @Test
    @DisplayName("[Service][Email] 인증 코드가 null 일 때 NO_VERIFICATION_CODE 에러 발생")
    void nullEmailVerificationCodeTest() throws Exception
    {
        //given
        String email = "test@principal.com";
        given(emailUtil.createMessage(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException = (CustomException) catchException(() -> sut.sendEmail(email));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.NO_VERIFICATION_CODE.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NO_VERIFICATION_CODE.getMessage());

        then(emailUtil).should().createMessage(any());
    }



}