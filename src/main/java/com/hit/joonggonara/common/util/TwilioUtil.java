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
public class TwilioUtil {


    private final String ACCOUNT_SID;
    private final String AUTH_TOKEN;
    private final String FROM_PHONE_NUMBER;

    public TwilioUtil(
            @Value("${twilio.account-sid}")
            String ACCOUNT_SID,
            @Value("${twilio.auth-token}")
            String AUTH_TOKEN,
            @Value("${twilio.phone-number}")
            String FROM_PHONE_NUMBER) {
        this.ACCOUNT_SID = ACCOUNT_SID;
        this.AUTH_TOKEN = AUTH_TOKEN;
        this.FROM_PHONE_NUMBER = FROM_PHONE_NUMBER;
    }

    public Optional<String> sendMessage(String toPhoneNumber){
        // 인증 코드 생성
        String verificationCode = createVerificationCode();

        String sendMessage = "[굿바이 굿] 인증번호는 (" + verificationCode + ")입니다.";
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message.creator(
                // 받을 번호
                new PhoneNumber(toPhoneNumber),
                // 보낼 번호
                new PhoneNumber(FROM_PHONE_NUMBER),
                // 보낼 메시지
                sendMessage
        ).create();

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
