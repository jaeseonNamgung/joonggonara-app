package com.hit.joonggonara.service.login;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.properties.RedisProperties;
import com.hit.joonggonara.common.util.EmailUtil;
import com.hit.joonggonara.common.util.RedisUtil;
import com.hit.joonggonara.common.util.TwilioUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VerificationService {


    private final RedisUtil redisUtil;
    private final TwilioUtil twilioUtil;
    private final EmailUtil emailUtil;

    // sms & email 인증 코드 검사
    public boolean checkVerificationCode(String key, String verificationCode){
        String code =  redisUtil.get(key)
                .orElseThrow(() -> new CustomException(UserErrorCode.VERIFICATION_CODE_TIME_OVER));

        return code.equals(verificationCode);
    }
    public void sendSms(String phoneNumber){
        String verificationCode = twilioUtil.sendMessage(phoneNumber)
                .orElseThrow(() -> new CustomException(UserErrorCode.NO_VERIFICATION_CODE));

        redisUtil.removeAndSave(
                RedisProperties.PHONE_NUMBER_KEY + phoneNumber,
                verificationCode,
                RedisProperties.PHONE_NUMBER_EXPIRATION_TIME
        );
    }
    public void sendEmail(String email){
        String verificationCode = emailUtil.createMessage(email)
                .orElseThrow(() -> new CustomException(UserErrorCode.NO_VERIFICATION_CODE));

        redisUtil.removeAndSave(
                RedisProperties.EMAIL_KEY + email,
                verificationCode,
                RedisProperties.EMAIL_EXPIRATION_TIME
        );

    }
}
