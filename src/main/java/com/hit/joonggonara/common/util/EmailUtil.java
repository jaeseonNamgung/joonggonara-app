package com.hit.joonggonara.common.util;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

import static com.hit.joonggonara.common.properties.EmailProperties.FROM_EMAIL;


@RequiredArgsConstructor
@Service
public class EmailUtil {

    private final AmazonSimpleEmailService amazonSimpleEmailService;


    public Optional<String> createMessage(String toEmail){
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        String verificationCode = createVerificationCode();
        String subject = "GoodbyeGood 인증 메일입니다.";

        String content = "";
        content += "<div style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 540px; height: 600px; border-top: 4px solid #02b875; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">\n" +
                "\t<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">\n" +
                "\t\t<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">HCC</span><br />\n" +
                "\t\t<span style=\"color: #02b875;\">메일인증</span> 안내입니다.\n" +
                "\t</h1>\n" +
                "\t<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">\n" +
                "\t\t안녕하세요.<br />\n" +
                "\t\tGoodbyeGood에 가입해 주셔서 진심으로 감사드립니다.<br />\n" +
                "\t\t인증번호: ";
        content += verificationCode;
        content += "<span style=\"font-size: 24px;\"></span>입니다.";

        sendEmailRequest.withSource(FROM_EMAIL)
                .withDestination(new Destination().withToAddresses(toEmail))
                .withMessage(
                        new Message()
                                .withSubject(encodingText(subject))
                                .withBody(new Body().withHtml(encodingText(content)))
                );

        amazonSimpleEmailService.sendEmail(sendEmailRequest);
        return Optional.of(verificationCode);
    }

    private Content encodingText(String text) {
        return new Content().withCharset(StandardCharsets.UTF_8.name()).withData(text);
    }

    private String createVerificationCode() {
        int length = 6;
        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(secureRandom.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(UserErrorCode.NO_SUCH_ALGORITHM);
        }
    }

}
