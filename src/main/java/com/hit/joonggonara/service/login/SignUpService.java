package com.hit.joonggonara.service.login;


import com.hit.joonggonara.common.properties.RedisProperties;
import com.hit.joonggonara.common.util.RedisUtil;
import com.hit.joonggonara.common.util.TwilioUtil;
import com.hit.joonggonara.dto.login.request.SignUpPhoneNumberRequest;
import com.hit.joonggonara.dto.request.login.SignUpRequest;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.dto.request.login.VerificationRequest;
import com.hit.joonggonara.repository.login.MemberRepository;
import com.twilio.Twilio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SignUpService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    @Transactional
    public boolean signUp(SignUpRequest signUpRequest){
        // 이미 회원 가입 되어 있는지 확인
        if (memberRepository.existByUserId(signUpRequest.email())){
            throw new CustomException(UserErrorCode.ALREADY_LOGGED_IN_USER);
        }
        // password 인코딩
        String passwordEncode = passwordEncoder.encode(signUpRequest.password());
        memberRepository.save(signUpRequest.toEntity(passwordEncode));
        return true;
    }

    // ID 중복 검사
    public boolean checkDuplicateUserId(String userId){
        if(memberRepository.existByUserId(userId)){
            throw new CustomException(UserErrorCode.EXIST_USER_ID);
        }
        return true;
    }

    // sms 인증 코드 발송
    @Transactional
    public boolean sendSmsVerificationCode(SignUpPhoneNumberRequest phoneNumberRequest){
        verificationService.sendSms(phoneNumberRequest.phoneNumber());
        return true;
    }

    // 인증 코드 검사
    @Transactional
    public boolean checkCode(VerificationRequest verificationRequest){
        String key = RedisProperties.PHONE_NUMBER_KEY + verificationRequest.verificationKey();
        return verificationService.checkVerificationCode(key, verificationRequest.verificationCode());
    }



}
