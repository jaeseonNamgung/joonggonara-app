package com.hit.joonggonara.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hit.joonggonara.common.custom.validation.ValidationSequence;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.VerificationType;
import com.hit.joonggonara.common.util.CookieUtil;
import com.hit.joonggonara.dto.request.login.*;
import com.hit.joonggonara.dto.response.board.MemberResponse;
import com.hit.joonggonara.dto.response.login.*;
import com.hit.joonggonara.service.login.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.hit.joonggonara.common.properties.JwtProperties.*;



@RequiredArgsConstructor
@RestController
public class LoginApiController {

    private final LoginService loginService;
    private final CookieUtil cookieUtil;


    @PostMapping("/user/login")
    public ResponseEntity<MemberResponse> login(
            HttpServletResponse response,
            @RequestBody @Valid LoginRequest loginRequest){
        MemberTokenResponse memberTokenResponse = loginService.login(loginRequest);
        saveAccessTokenAndRefreshToken(response, memberTokenResponse.accessToken(), memberTokenResponse.refreshToken());
        return ResponseEntity.ok(memberTokenResponse.memberResponse());
    }


    @GetMapping("/user/login/oauth2")
    public ResponseEntity<String> sendOAuth2LoginPage(@RequestParam(name = "loginType") String loginType) throws IOException {
        return ResponseEntity.ok(loginService.sendRedirect(LoginType.checkType(loginType)));
    }

    @GetMapping("/user/login/oauth2/code/{loginType}")
    public ResponseEntity<OAUth2UserResponse> OAuth2Login(@RequestParam(name = "code") String code,
                                                          @PathVariable(name = "loginType") String loginType,
                                                          HttpServletResponse response) throws JsonProcessingException {
        OAuth2UserDto oAuth2UserDto = loginService.oAuth2Login(code, LoginType.checkType(loginType));
        if(oAuth2UserDto.signUpStatus()){
            saveAccessTokenAndRefreshToken(response, oAuth2UserDto.accessToken(), oAuth2UserDto.refreshToken());
            return ResponseEntity.ok(OAUth2UserResponse.fromResponse(oAuth2UserDto.principal(), oAuth2UserDto.profile(),true));
        }
        return ResponseEntity.ok(OAUth2UserResponse.fromResponse(oAuth2UserDto.principal(),oAuth2UserDto.profile(), false));
    }

    @PutMapping("/user/login/reissue")
    public ResponseEntity<Boolean> reissue(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = cookieUtil.getCookie(request)
                .map(Cookie::getValue)
                .orElseThrow(() -> new CustomException(UserErrorCode.ALREADY_LOGGED_OUT_USER));
        TokenResponse memberTokenResponse = loginService.reissueToken(refreshToken);

        // 새로 발급 받은 AccessToken Header에 저장
        response.setHeader(AUTHORIZATION, JWT_TYPE + memberTokenResponse.accessToken());

        // 새로 발급 받은 RefreshToken Cookie에 저장
        cookieUtil.addCookie(response, REFRESH_TOKEN_NAME, memberTokenResponse.refreshToken());
        return ResponseEntity.ok(true);
    }

    // 핸드폰 번호로 아이디 찾기
    @PostMapping("/user/login/findId/sms")
    public ResponseEntity<Boolean> findUserIdBySms(@RequestBody @Valid FindUserIdBySmsRequest findUserIdBySmsRequest){
        return ResponseEntity.ok(loginService.findUserIdBySms(findUserIdBySmsRequest));
    }
    // 이메일로 아이디 찾기
    @PostMapping("/user/login/findId/email")
    public ResponseEntity<Boolean> findUserIdByEmail(@RequestBody @Validated(ValidationSequence.class)
                                                         FindUserIdByEmailRequest findUserIdByEmailRequest){
        return ResponseEntity.ok(loginService.findUserIdByEmail(findUserIdByEmailRequest));
    }
    // 핸드폰 번호로 패스워드 찾기
    @PostMapping("/user/login/findPassword/sms")
    public ResponseEntity<Boolean> findPasswordBySms(@RequestBody @Valid FindPasswordBySmsRequest findPasswordBySmsRequest){
        return ResponseEntity.ok(loginService.findPasswordBySms(findPasswordBySmsRequest));
    }
    // 이메일로 패스워드 찾기
    @PostMapping("/user/login/findPassword/email")
    public ResponseEntity<Boolean> findPasswordByEmail(@RequestBody @Validated(ValidationSequence.class)
                                                           FindPasswordByEmailRequest findPasswordByEmailRequest){
        return ResponseEntity.ok(loginService.findPasswordByEmail(findPasswordByEmailRequest));
    }

    @PostMapping("/user/login/checkVerificationCode/userId")
    public ResponseEntity<FindUserIdResponse> checkUserIdVerificationCode(
            @RequestBody @Valid VerificationRequest verificationRequest,
            @RequestParam(name = "verificationType") String verificationType
    ){

        return ResponseEntity.ok(
                loginService.checkUserIdVerificationCode(
                        verificationRequest, VerificationType.toEnum(verificationType)
                ));
    }

    @PostMapping("/user/login/checkVerificationCode/password")
    public ResponseEntity<Boolean> checkPasswordVerificationCode(
            @RequestBody @Valid VerificationRequest verificationRequest,
            @RequestParam(name = "verificationType") String verificationType
    ){
        return ResponseEntity.ok(
                loginService.checkPasswordVerificationCode(
                        verificationRequest, VerificationType.toEnum(verificationType)
                ));
    }

    @PutMapping("/user/login/update/password")
    public ResponseEntity<Boolean> updatePassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest){
        return ResponseEntity.ok(loginService.updatePassword(updatePasswordRequest));
    }


    private void saveAccessTokenAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addHeader(AUTHORIZATION, JWT_TYPE + accessToken);
        cookieUtil.addCookie(response, REFRESH_TOKEN_NAME, refreshToken);
    }




}
