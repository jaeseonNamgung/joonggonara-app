package com.hit.joonggonara.common.util;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

@Service
public class TwilioSmsUtil {


    private final String SID;
    private final String TOKEN;
    private final String FROM_PHONE_NUMBER;

    public TwilioSmsUtil(
            @Value("${twilio.account-sid}")
            String SID,
            @Value("${twilio.auth-token}")
            String TOKEN,
            @Value("${twilio.phone-number}")
            String FROM_PHONE_NUMBER) {
        this.SID = SID;
        this.TOKEN = TOKEN;
        this.FROM_PHONE_NUMBER = FROM_PHONE_NUMBER;
    }

    public Optional<String> sendMessage(String toPhoneNumber){
        // 인증 코드 생성
        Twilio.init(SID, TOKEN);
        String verificationCode = createVerificationCode();
        String sendMessage = "[굿바이 굿] 인증번호는 (" + verificationCode + ")입니다.";

        try {
            Message.creator(
                    new PhoneNumber("+82" + toPhoneNumber),
                    new PhoneNumber(FROM_PHONE_NUMBER),
                    sendMessage
            ).create();
        } catch (Exception e){
            throw new CustomException(UserErrorCode.SEND_ERROR);
        }

        return Optional.of(verificationCode);
    }

    private String createVerificationCode() {
        int length = 6;

        try{
            SecureRandom random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(UserErrorCode.NO_SUCH_ALGORITHM);
        }

    }

}
