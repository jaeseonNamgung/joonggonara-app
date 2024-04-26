package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.custom.validation.ValidationSequence;
import com.hit.joonggonara.dto.request.LoginRequest;
import com.hit.joonggonara.dto.request.PhoneNumberRequest;
import com.hit.joonggonara.dto.request.SmsVerificationRequest;
import com.hit.joonggonara.dto.response.TokenResponse;
import com.hit.joonggonara.properties.JwtProperties;
import com.hit.joonggonara.service.user.LoginService;
import com.hit.joonggonara.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class LoginApiController {

    private final LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<Boolean> login(
            HttpServletResponse response,
            // ValidationSequence를 사용하기 위해서 @Valid -> Validated 로 교체
            @RequestBody @Validated(ValidationSequence.class) LoginRequest loginRequest){
        TokenResponse tokenResponse = loginService.login(loginRequest);
        response.addHeader(JwtProperties.JWT_TYPE, JwtProperties.JWT_TYPE  + " " + tokenResponse.accessToken());
        CookieUtil.addCookie(response, JwtProperties.REFRESH_TOKEN_NAME, tokenResponse.refreshToken());

        return ResponseEntity.ok(true);
    }


    @PostMapping("/login/checkPhoneNumber")
    public ResponseEntity<Boolean> checkPhoneNumber(@RequestBody @Valid PhoneNumberRequest phoneNumberRequest){
        return ResponseEntity.ok(loginService.checkPhoneNumber(phoneNumberRequest));
    }

    @PostMapping("/login/checkVerificationCode")
    public ResponseEntity<Boolean> checkVerificationCode(@RequestBody @Valid SmsVerificationRequest smsVerificationRequest){
        return ResponseEntity.ok(loginService.checkSmsVerificationCode(smsVerificationRequest));
    }

}
