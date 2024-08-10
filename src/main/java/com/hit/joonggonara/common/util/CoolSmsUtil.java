package com.hit.joonggonara.common.util;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Optional;

@Service
public class CoolSmsUtil {


    private final String API_KEY;
    private final String SECRET;
    private final String FROM_PHONE_NUMBER;

    public CoolSmsUtil(
            @Value("${coolsms.api_key}")
            String API_KEY,
            @Value("${coolsms.secret}")
            String SECRET,
            @Value("${coolsms.phone_number}")
            String FROM_PHONE_NUMBER) {
        this.API_KEY = API_KEY;
        this.SECRET = SECRET;
        this.FROM_PHONE_NUMBER = FROM_PHONE_NUMBER;
    }

    public Optional<String> sendMessage(String toPhoneNumber){
        // 인증 코드 생성
        String verificationCode = createVerificationCode();

        String sendMessage = "[굿바이 굿] 인증번호는 (" + verificationCode + ")입니다.";

        HashMap<String, String> params = new HashMap<>();
        Message coolsms = new Message(API_KEY, SECRET);

        params.put("to", toPhoneNumber);
        params.put("from", FROM_PHONE_NUMBER);
        params.put("type", "SMS");
        params.put("text", sendMessage);

        try{
            coolsms.send(params);
        }catch (CoolsmsException e){
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
