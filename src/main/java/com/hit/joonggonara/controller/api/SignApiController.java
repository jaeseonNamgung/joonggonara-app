package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.common.custom.validation.ValidationSequence;
import com.hit.joonggonara.dto.request.login.SignUpPhoneNumberRequest;
import com.hit.joonggonara.dto.request.login.SignUpRequest;
import com.hit.joonggonara.dto.request.login.VerificationRequest;
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
        return ResponseEntity.ok(signUpService.signUp(signUpRequest));
    }


    @GetMapping("/user/signUp/duplicateUserId")
    public ResponseEntity<Boolean> duplicateUserId(@RequestParam("userId") String userId){
        return ResponseEntity.ok(signUpService.checkDuplicateUserId(userId));
    }
    @GetMapping("/user/signUp/duplicateNickName")
    public ResponseEntity<Boolean> duplicateNickName(@RequestParam("nickName") String nickName){
        boolean isDuplicate = signUpService.checkDuplicateNickName(nickName);
        return ResponseEntity.ok(isDuplicate);
    }

    @PostMapping("/user/signUp/sms/verification")
    public ResponseEntity<Boolean> sendSms(@RequestBody SignUpPhoneNumberRequest phoneNumberRequest){
        return ResponseEntity.ok(signUpService.sendSmsVerificationCode(phoneNumberRequest));
    }

    @PostMapping("/user/signUp/sms/checkCode")
    public ResponseEntity<Boolean> checkCode(@RequestBody @Valid VerificationRequest verificationRequest){
        return ResponseEntity.ok(signUpService.checkCode(verificationRequest));
    }

    @GetMapping("/user/signUp/nickName/{nickName}")
    public ResponseEntity<Boolean> checkNickName(
            @PathVariable(name = "nickName") String nickName
    ){
        return ResponseEntity.ok(signUpService.checkNickName(nickName));
    }


}
