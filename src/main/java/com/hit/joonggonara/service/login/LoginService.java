package com.hit.joonggonara.service.user;

import com.hit.joonggonara.custom.login.CustomUserProvider;
import com.hit.joonggonara.dto.TokenDto;
import com.hit.joonggonara.dto.request.*;
import com.hit.joonggonara.dto.response.TokenResponse;
import com.hit.joonggonara.error.CustomException;
import com.hit.joonggonara.error.errorCode.UserErrorCode;
import com.hit.joonggonara.properties.RedisProperties;
import com.hit.joonggonara.util.EmailUtil;
import com.hit.joonggonara.util.JwtUtil;
import com.hit.joonggonara.type.LoginType;
import com.hit.joonggonara.type.Role;
import com.hit.joonggonara.util.RedisUtil;
import com.hit.joonggonara.util.TwilioUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LoginService {

    private final CustomUserProvider userProvider;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final TwilioUtil twilioUtil;
    private final EmailUtil emailUtil;

    @Transactional
    public TokenResponse login(LoginRequest loginRequest){

        // 초기 인증 정보를 넣는다.
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());

        Authentication authenticate = userProvider.authenticate(authentication);

        // 회원 Role을 꺼내옴
        String r = authenticate.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst()
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_EXIST_AUTHORIZATION));
        Role role = Role.checkRole(r);

        // access token, refresh token 생성
        TokenDto token = createToken((String) authenticate.getPrincipal(), role);

        return TokenResponse.ToResponse(token);
    }

    private TokenDto createToken(String email, Role role) {

        TokenDto token = jwtUtil.getToken(email, role, LoginType.GENERAL);
        saveRefreshToken(email, token.refreshToken());
        return token;
    }


    private void saveRefreshToken(String email, String refreshToken) {

        Optional<String> optionalRefreshToken = redisUtil.get(RedisProperties.REFRESH_TOKEN_KEY + refreshToken);

        // refreshToken 이 존재 할경우 이미 로그인한 유저로 판단
        if(optionalRefreshToken.isPresent()){
            throw new CustomException(UserErrorCode.ALREADY_LOGGED_IN_USER);
        }

        redisUtil.save(
                RedisProperties.REFRESH_TOKEN_KEY + email,
                refreshToken,
                RedisProperties.REFRESH_TOKEN_EXPIRATION_TIME
                );
    }

    public boolean checkPhoneNumber(PhoneNumberRequest phoneNumberRequest){
        String verificationCode = twilioUtil.sendMessage(phoneNumberRequest.phoneNumber())
                .orElseThrow(() -> new CustomException(UserErrorCode.NO_RANDOM_NUMBER));

        redisUtil.save(
                RedisProperties.REFRESH_TOKEN_KEY + phoneNumberRequest.phoneNumber(),
                verificationCode,
                RedisProperties.PHONE_NUMBER_EXPIRATION_TIME
        );
        return true;
    }

    public boolean checkSmsVerificationCode(SmsVerificationRequest smsVerificationRequest){
        String verificationCode = redisUtil.get(RedisProperties.PHONE_NUMBER_KEY + smsVerificationRequest.phoneNumber())
                .orElseThrow(() -> new CustomException(UserErrorCode.NO_VERIFICATION_CODE));

        return verificationCode.equals(smsVerificationRequest.verificationCode());
    }

    public boolean checkEmail(EmailRequest emailRequest){
        String verificationCode = emailUtil.createMessage(emailRequest.email())
                .orElseThrow(() -> new CustomException(UserErrorCode.NO_VERIFICATION_CODE));

        redisUtil.save(
                RedisProperties.EMAIL_KEY + emailRequest.email(),
                verificationCode,
                RedisProperties.EMAIL_EXPIRATION_TIME
        );

        return true;
    }

    public boolean checkEmailVerificationCode(EmailVerificationRequest emailVerificationRequest){
        String verificationCode = redisUtil.get(RedisProperties.EMAIL_KEY + emailVerificationRequest.email())
                .orElseThrow(() -> new CustomException(UserErrorCode.NO_VERIFICATION_CODE));

        return verificationCode.equals(emailVerificationRequest.verificationCode());
    }

}
