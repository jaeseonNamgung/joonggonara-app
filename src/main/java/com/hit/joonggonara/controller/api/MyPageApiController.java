package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.common.custom.validation.ValidationSequence;
import com.hit.joonggonara.dto.request.login.MemberUpdateRequest;
import com.hit.joonggonara.dto.request.login.SignUpPhoneNumberRequest;
import com.hit.joonggonara.dto.request.login.VerificationRequest;
import com.hit.joonggonara.dto.response.board.MemberResponse;
import com.hit.joonggonara.dto.response.board.ProductResponse;
import com.hit.joonggonara.service.board.ProductService;
import com.hit.joonggonara.service.login.LoginService;
import com.hit.joonggonara.service.login.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.hit.joonggonara.common.properties.JwtProperties.AUTHORIZATION;

@RequiredArgsConstructor
@RestController
public class MyPageApiController {

    private final SignUpService signUpService;
    private final LoginService loginService;
    private final ProductService productService;


    @PostMapping(path = "/user/update/info" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberResponse> memberUpdateInfo(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestPart MultipartFile profile,
            @RequestPart @Validated(ValidationSequence.class) MemberUpdateRequest memberUpdateRequest
            ){

        return ResponseEntity.ok(loginService.memberUpdateInfo(token, memberUpdateRequest, profile));
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

    @GetMapping("/product/{nickName}")
    public ResponseEntity<List<ProductResponse>> getSaleCount(@PathVariable(name = "nickName") String nickName){
        return ResponseEntity.ok(productService.getProduct(nickName));
    }
    @DeleteMapping("/product/delete/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable(name = "productId") Long productId){
        return ResponseEntity.ok(productService.delete(productId));
    }
}
