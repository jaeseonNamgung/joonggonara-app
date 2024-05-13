package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.common.custom.validation.ValidationSequence;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.dto.request.login.SignUpPhoneNumberRequest;
import com.hit.joonggonara.dto.request.login.SignUpRequest;
import com.hit.joonggonara.dto.request.login.VerificationRequest;
import com.hit.joonggonara.dto.response.login.SignUpOAuth2Response;
import com.hit.joonggonara.service.login.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class SignApiController {

    private final SignUpService signUpService;


    @PostMapping("/user/signUp")
    public ResponseEntity<Boolean> signUp(
            @RequestBody @Validated(ValidationSequence.class) SignUpRequest signUpRequest
            ){
        boolean isTrue = signUpService.signUp(signUpRequest);
        return ResponseEntity.ok(isTrue);
    }


    @GetMapping("/user/signUp/duplicateUserId")
    public ResponseEntity<Boolean> duplicateUserId(@RequestParam("userId") String userId){
        boolean isDuplicate = signUpService.checkDuplicateUserId(userId);
        return ResponseEntity.ok(isDuplicate);
    }

    @PostMapping("/user/signUp/sms/verification")
    public ResponseEntity<Boolean> sendSms(@RequestBody SignUpPhoneNumberRequest phoneNumberRequest){
        boolean isVerificationCode = signUpService.sendSmsVerificationCode(phoneNumberRequest);
        return ResponseEntity.ok(isVerificationCode);
    }

    @PostMapping("/user/signUp/sms/checkCode")
    public ResponseEntity<Boolean> checkCode(@RequestBody @Valid VerificationRequest verificationRequest){
        boolean isCode = signUpService.checkCode(verificationRequest);
        return ResponseEntity.ok(isCode);
    }


}
