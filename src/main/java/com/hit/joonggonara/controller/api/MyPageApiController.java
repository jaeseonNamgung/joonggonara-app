package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.common.custom.validation.ValidationSequence;
import com.hit.joonggonara.dto.request.login.MemberUpdateRequest;
import com.hit.joonggonara.dto.request.login.SignUpPhoneNumberRequest;
import com.hit.joonggonara.dto.request.login.VerificationRequest;
import com.hit.joonggonara.service.login.LoginService;
import com.hit.joonggonara.service.login.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.hit.joonggonara.common.properties.JwtProperties.AUTHORIZATION;

@RequiredArgsConstructor
@RestController
public class MyPageApiController {

    private final SignUpService signUpService;
    private final LoginService loginService;


    @PostMapping("/user/update/info")
    public ResponseEntity<Boolean> memberUpdateInfo(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestBody @Validated(ValidationSequence.class) MemberUpdateRequest memberUpdateRequest
            ){
        loginService.memberUpdateInfo(token, memberUpdateRequest);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/user/update/info/nickName/{nickName}")
    public ResponseEntity<Boolean> checkNickName(
            @PathVariable(name = "nickName") String nickName
    ){
        return ResponseEntity.ok(signUpService.checkNickName(nickName));
    }

    @PostMapping("/user/update/info/sms/verification")
    public ResponseEntity<Boolean> sendSms(@RequestBody SignUpPhoneNumberRequest phoneNumberRequest){
        return ResponseEntity.ok(signUpService.sendSmsVerificationCode(phoneNumberRequest));
    }
    @PostMapping("/user/update/info/sms/checkCode")
    public ResponseEntity<Boolean> checkCode(@RequestBody @Valid VerificationRequest verificationRequest){
        return ResponseEntity.ok(signUpService.checkCode(verificationRequest));
    }
}
