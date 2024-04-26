package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.common.custom.validation.ValidationSequence;
import com.hit.joonggonara.dto.request.login.SignUpRequest;
import com.hit.joonggonara.service.login.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SignApiController {

    private final SignUpService signUpService;


    @PostMapping("/signUp")
    public ResponseEntity<Boolean> signUp(
            @RequestBody @Validated(ValidationSequence.class) SignUpRequest signUpRequest
            ){
        boolean isTrue = signUpService.signUp(signUpRequest);
        return ResponseEntity.ok(isTrue);
    }
}
