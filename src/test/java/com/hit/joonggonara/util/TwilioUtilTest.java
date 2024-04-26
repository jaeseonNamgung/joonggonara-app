package com.hit.joonggonara.util;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.util.TwilioUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@TestPropertySource(properties = {"spring.config.location = classpath:application.yml"})
@ActiveProfiles("test")
@Import(TwilioUtil.class)
@ExtendWith(MockitoExtension.class)
class TwilioUtilTest {


    @InjectMocks
    private TwilioUtil twilioUtil;

    
    @Test
    @DisplayName("[Twilio] 문자 인증 테스트")
    void sendMessageTest() throws Exception
    {

        //given
        String authToken = "test_auth_token";
        String accountSid = "test_account_sid";
        String phoneNumber = "test_phone_number";
        String toPhoneNumber = "+8617545562261";
        //when
        twilioUtil = new TwilioUtil(accountSid, authToken, phoneNumber);
        String expectedNumber = twilioUtil.sendMessage(toPhoneNumber).get();

        //then
        assertThat(expectedNumber).isNotEmpty();
        assertThat(expectedNumber).hasSize(6);
    }

    @Test
    @DisplayName("[Twilio][Exception] NoSuchAlgorithmException 테스트")
    void testNoSuchAlgorithmExceptionHandling() throws Exception
    {
        //given
        String phoneNumber = "+8612345678";

        try(MockedStatic<SecureRandom> mock = Mockito.mockStatic(SecureRandom.class)){
            mock.when(SecureRandom::getInstanceStrong).thenThrow(NoSuchAlgorithmException.class);
            //when
            CustomException expectedException =
                    (CustomException)catchException(()->twilioUtil.sendMessage(phoneNumber));
            //then
            assertThat(expectedException.getErrorCode().getHttpStatus())
                    .isEqualTo(UserErrorCode.NO_SUCH_ALGORITHM.getHttpStatus());
            assertThat(expectedException).hasMessage(UserErrorCode.NO_SUCH_ALGORITHM.getMessage());
        }
    }
}