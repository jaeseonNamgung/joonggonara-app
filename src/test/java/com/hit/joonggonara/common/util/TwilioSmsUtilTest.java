package com.hit.joonggonara.common.util;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
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
@Import(TwilioSmsUtil.class)
@ExtendWith(MockitoExtension.class)
class TwilioSmsUtilTest {


    @InjectMocks
    private TwilioSmsUtil twilioSmsUtil;


    @Test
    @DisplayName("[CoolSms] 메세지 전송 오류")
    void sendMessageTest() throws Exception
    {

        //given
        String authToken = "test_auth_token";
        String accountSid = "test_account_sid";
        String phoneNumber = "01097175449";
        String toPhoneNumber = "01097175449";
        //when
        twilioSmsUtil = new TwilioSmsUtil(accountSid, authToken, phoneNumber);
        CustomException customException =
                (CustomException) catchException(() -> twilioSmsUtil.sendMessage(toPhoneNumber));

        //then
        assertThat(customException.getMessage()).isEqualTo(UserErrorCode.SEND_ERROR.getMessage());
        assertThat(customException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.SEND_ERROR.getHttpStatus());
    }

    @Test
    @DisplayName("[CoolSms][Exception] NoSuchAlgorithmException 테스트")
    void testNoSuchAlgorithmExceptionHandling() throws Exception
    {
        //given
        String phoneNumber = "01097175449";

        try(MockedStatic<SecureRandom> mock = Mockito.mockStatic(SecureRandom.class)){
            mock.when(SecureRandom::getInstanceStrong).thenThrow(NoSuchAlgorithmException.class);
            //when
            CustomException expectedException =
                    (CustomException)catchException(()-> twilioSmsUtil.sendMessage(phoneNumber));
            //then
            assertThat(expectedException.getErrorCode().getHttpStatus())
                    .isEqualTo(UserErrorCode.NO_SUCH_ALGORITHM.getHttpStatus());
            assertThat(expectedException).hasMessage(UserErrorCode.NO_SUCH_ALGORITHM.getMessage());
        }
    }
}
