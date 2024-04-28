package com.hit.joonggonara.service.login;

import com.hit.joonggonara.common.custom.login.CustomUserProvider;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.common.type.VerificationType;
import com.hit.joonggonara.common.util.EmailUtil;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.common.util.RedisUtil;
import com.hit.joonggonara.common.util.TwilioUtil;
import com.hit.joonggonara.dto.login.TokenDto;
import com.hit.joonggonara.dto.request.login.*;
import com.hit.joonggonara.dto.response.login.TokenResponse;
import com.hit.joonggonara.repository.login.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CustomUserProvider userProvider;
    @Mock
    private RedisUtil redisUtil;
    @Mock
    private TwilioUtil twilioUtil;
    @Mock
    private EmailUtil emailUtil;

    @InjectMocks
    private LoginService sut;

    @Test
    @DisplayName("[Service] login 성공 시 TokenResponse 리턴")
    void loginSuccessTest() throws Exception
    {
        //given
        TokenDto tokenDto = createTokenDto();
        Authentication authentication = createUsernamePasswordAuthenticationToken();
        LoginRequest loginRequest = createLoginRequest();
        given(userProvider.authenticate(any())).willReturn(authentication);
        given(jwtUtil.getToken(any(), any(), any())).willReturn(tokenDto);
        given(redisUtil.get(any())).willReturn(Optional.empty());
        //when
        TokenResponse expectedTokenResponse = sut.login(loginRequest);
        //then
        assertThat(expectedTokenResponse.accessToken()).isEqualTo(tokenDto.accessToken());
        assertThat(expectedTokenResponse.refreshToken()).isEqualTo(tokenDto.refreshToken());
       

        then(userProvider).should().authenticate(any());
        then(jwtUtil).should().getToken(any(),any(),any());
        then(redisUtil).should().get(any());
        then(redisUtil).should().removeAndSave(any(), any(), any());
    }
    
    @Test
    @DisplayName("[Service] 존재 하지 않은 권한 정보 일 경우 NOT_EXIST_AUTHORIZATION Exception 발생")
    void notExistAuthorization() throws Exception
    {
        //given
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testId", "abc1234");
        LoginRequest loginRequest = createLoginRequest();
        given(userProvider.authenticate(any())).willReturn(authentication);
        //when
        CustomException expectedException =
                (CustomException)catchRuntimeException(()-> sut.login(loginRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.NOT_EXIST_AUTHORIZATION.getHttpStatus());
        assertThat(expectedException.getMessage()).isEqualTo(UserErrorCode.NOT_EXIST_AUTHORIZATION.getMessage());
        then(userProvider).should().authenticate(any());
    }
    
    @Test
    @DisplayName("[Service] 이미 존재 하는 refreshToken 일 경우 ALREADY_LOGGED_IN_USER Exception 발생")
    void alreadyLoggedInUserTest() throws Exception
    {
        //given
        TokenDto tokenDto = createTokenDto();
        Authentication authentication = createUsernamePasswordAuthenticationToken();
        LoginRequest loginRequest = createLoginRequest();
        String refreshToken = "refreshToken";
        given(userProvider.authenticate(any())).willReturn(authentication);
        given(jwtUtil.getToken(any(), any(), any())).willReturn(tokenDto);
        given(redisUtil.get(any())).willReturn(Optional.of(refreshToken));
        //when
       CustomException expectedException =
               (CustomException)catchRuntimeException(()-> sut.login(loginRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.ALREADY_LOGGED_IN_USER.getHttpStatus());
        assertThat(expectedException.getMessage()).isEqualTo(UserErrorCode.ALREADY_LOGGED_IN_USER.getMessage());

        then(userProvider).should().authenticate(any());
        then(jwtUtil).should().getToken(any(),any(),any());
        then(redisUtil).should().get(any());
    }

    @Test
    @DisplayName("[Service][Sms][아이디 찾기]  회원 정보가 존재하고  핸드폰 인증코드가 정상적으로 발송 및 Redis에 저장 될 경우 true를 리턴")
    void checkExistUserAndReceivingVerificationCodeTest() throws Exception
    {
        //given
        FindUserIdBySmsRequest findUserIdBySmsRequest = createFindUserIdSmsRequest();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any())).willReturn(true);
        given(twilioUtil.sendMessage(any())).willReturn(Optional.of("+8612345678"));

        //when
        boolean expectedValue = sut.findUserIdBySms(findUserIdBySmsRequest);
        //then
        assertThat(expectedValue).isTrue();

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
        then(redisUtil).should().removeAndSave(any(),any(),any());
        then(twilioUtil).should().sendMessage(any());
    }
    @Test
    @DisplayName("[Service][Sms][아이디 찾기] 회원 정보가 존재하지 않을 경우 NO_EXIST_USER 에러 발생")
    void notExistUserTest() throws Exception
    {
        //given
        FindUserIdBySmsRequest findUserIdBySmsRequest = createFindUserIdSmsRequest();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any())).willReturn(false);

        //when
        CustomException expectedException =
                (CustomException)catchException(()->sut.findUserIdBySms(findUserIdBySmsRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.NOT_EXIST_USER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NOT_EXIST_USER.getMessage());

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
    }

    
    @Test
    @DisplayName("[Service][Sms] 정확한 핸드폰 인증 코드 일 때 true를 리턴")
    void smsVerificationCodeSuccessTest() throws Exception
    {
        //given
        VerificationRequest verificationRequest = createSmsVerificationRequest("123456");
        VerificationType verificationType = VerificationType.SMS;
        String verificationCode = "123456";
        given(redisUtil.get(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.checkVerificationCode(verificationRequest, verificationType);

        //then
        assertThat(expectedValue).isTrue();

        then(redisUtil).should().get(any());

    }
    @Test
    @DisplayName("[Service][Sms] 핸드폰 인증 코드가 다를 때 false를 리턴")
    void differentSmsVerificationCodeTest() throws Exception
    {
        //given
        String verificationCode = "123456";
        VerificationType verificationType = VerificationType.SMS;
        VerificationRequest verificationRequest = createSmsVerificationRequest("456789");
        given(redisUtil.get(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.checkVerificationCode(verificationRequest, verificationType);

        //then
        assertThat(expectedValue).isFalse();

        then(redisUtil).should().get(any());

    }

    @Test
    @DisplayName("[Service][Sms] 레디스에 인증 코드가 null 일경우 NO_VERIFICATION_CODE 에러 발생")
    void noSmsVerificationCodeTest() throws Exception
    {
        //given
        VerificationRequest verificationRequest = createSmsVerificationRequest("123456");
        VerificationType verificationType = VerificationType.SMS;
        given(redisUtil.get(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException = (CustomException) catchException(() ->
                sut.checkVerificationCode(verificationRequest, verificationType));

        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.NO_VERIFICATION_CODE.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NO_VERIFICATION_CODE.getMessage());

        then(redisUtil).should().get(any());
    }

    
    @Test
    @DisplayName("[Service][Email][아이디 찾기] 회원 정보가 존재하고  이메일 인증코드가 정상적으로 발송 및 Redis에 저장 될 경우 true를 리턴")
    void findUserIdByEmailSuccessTest() throws Exception
    {
        //given
        String verificationCode = "123456";
        FindUserIdByEmailRequest emailRequest = createFindIdEmailRequest();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any()))
                .willReturn(true);
        given(emailUtil.createMessage(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.findUserIdByEmail(emailRequest);
        //then
        assertThat(expectedValue).isTrue();

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
        then(emailUtil).should().createMessage(any());
        then(redisUtil).should().removeAndSave(any(), any(), any());
    }

    @Test
    @DisplayName("[Service][Email][아이디 찾기] 인증 코드가 null 일 때 NO_VERIFICATION_CODE 에러 발생")
    void nullEmailVerificationCodeTest() throws Exception
    {
        //given
        FindUserIdByEmailRequest emailRequest = createFindIdEmailRequest();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any()))
                .willReturn(true);
        given(emailUtil.createMessage(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException = (CustomException) catchException(() -> sut.findUserIdByEmail(emailRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.NO_VERIFICATION_CODE.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NO_VERIFICATION_CODE.getMessage());

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
        then(emailUtil).should().createMessage(any());
    }

    @Test
    @DisplayName("[Service][Email][아이디 찾기] 회원 정보가 존재하지 않을 경우 NO_EXIST_USER 에러 발생")
    void notExistUserByEmailTest() throws Exception
    {
        //given
        FindUserIdByEmailRequest emailRequest = createFindIdEmailRequest();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any())).willReturn(false);

        //when
        CustomException expectedException =
                (CustomException)catchException(()->sut.findUserIdByEmail(emailRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.NOT_EXIST_USER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NOT_EXIST_USER.getMessage());

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
    }


    @Test
    @DisplayName("[Service][Email] 정확한 이메일 인증 코드 일 때 true를 리턴")
    void checkEmailVerificationCodeSuccessTest() throws Exception
    {
        //given
        String verificationCode = "123456";
        VerificationRequest verificationRequest =
                createEmailVerificationRequest();
        VerificationType verificationType = VerificationType.EMAIL;
        given(redisUtil.get(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.checkVerificationCode(verificationRequest, verificationType);
        //then
        assertThat(expectedValue).isTrue();

        then(redisUtil).should().get(any());
    }

    @Test
    @DisplayName("[Service][Email][아이디 찾기] 레디스에 인증코드가 존재하지 않을때 No_VERIFICATION_CODE Exception 발생")
    void noEmailVerificationCodeInRedisTest() throws Exception
    {
        //given
        VerificationRequest verificationRequest =
                createEmailVerificationRequest();
        VerificationType verificationType = VerificationType.EMAIL;
        given(redisUtil.get(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException =
                (CustomException) catchException(() -> sut.checkVerificationCode(verificationRequest, verificationType));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.NO_VERIFICATION_CODE.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NO_VERIFICATION_CODE.getMessage());

        then(redisUtil).should().get(any());
    }
    
    @Test
    @DisplayName("[Service] 인증 코드가 다를 때 false를 리턴")
    void differentEmailVerificationCodeTest() throws Exception
    {
        //given
        String verificationCode = "456789";
        VerificationRequest verificationRequest =
                createEmailVerificationRequest();
        VerificationType verificationType = VerificationType.EMAIL;
        given(redisUtil.get(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.checkVerificationCode(verificationRequest, verificationType);
        //then
        assertThat(expectedValue).isFalse();

        then(redisUtil).should().get(any());
    }

    @Test
    @DisplayName("[Service][Sms][패스워드 찾기] 회원 정보가 존재하고  핸드폰 인증코드가 정상적으로 발송 및 Redis에 저장 될 경우 true를 리턴")
    void findPassword_checkExistUserAndReceivingVerificationCodeTest() throws Exception
    {
        //given
        FindPasswordBySmsRequest findPasswordBySmsRequest = createFindPasswordSmsRequest();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any())).willReturn(true);
        given(twilioUtil.sendMessage(any())).willReturn(Optional.of("+8612345678"));

        //when
        boolean expectedValue = sut.findPasswordBySms(findPasswordBySmsRequest);
        //then
        assertThat(expectedValue).isTrue();

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
        then(redisUtil).should().removeAndSave(any(),any(),any());
        then(twilioUtil).should().sendMessage(any());
    }


    @Test
    @DisplayName("[Service][Sms][패스워드 찾기] 회원 정보가 존재하지 않을 경우 NO_EXIST_USER 에러 발생")
    void findPassword_notExistUserTest() throws Exception
    {
        //given
        FindPasswordBySmsRequest findPasswordBySmsRequest = createFindPasswordSmsRequest();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any())).willReturn(false);

        //when
        CustomException expectedException =
                (CustomException)catchException(()->sut.findPasswordBySms(findPasswordBySmsRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.NOT_EXIST_USER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NOT_EXIST_USER.getMessage());

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
    }

    @Test
    @DisplayName("[Service][Email][패스워드 찾기] 회원 정보가 존재하고  이메일 인증코드가 정상적으로 발송 및 Redis에 저장 될 경우 true를 리턴")
    void findPassword_emailVerificationCodeSuccessTest() throws Exception
    {
        //given
        String verificationCode = "123456";
        FindPasswordByEmailRequest emailRequest = createFindPasswordEmail();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any()))
                .willReturn(true);
        given(emailUtil.createMessage(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.findPasswordByEmail(emailRequest);
        //then
        assertThat(expectedValue).isTrue();

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
        then(emailUtil).should().createMessage(any());
        then(redisUtil).should().removeAndSave(any(), any(), any());
    }



    @Test
    @DisplayName("[Service][Email][패스워드 찾기] 인증 코드가 null 일 때 NO_VERIFICATION_CODE 에러 발생")
    void findPassword_nullEmailVerificationCodeTest() throws Exception
    {
        //given
        FindPasswordByEmailRequest emailRequest = createFindPasswordEmail();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any()))
                .willReturn(true);
        given(emailUtil.createMessage(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException = (CustomException) catchException(() -> sut.findPasswordByEmail(emailRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.NO_VERIFICATION_CODE.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NO_VERIFICATION_CODE.getMessage());

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
        then(emailUtil).should().createMessage(any());
    }

    @Test
    @DisplayName("[Service][Email][패스워드 찾기] 회원 정보가 존재하지 않을 경우 NO_EXIST_USER 에러 발생")
    void findPassword_notExistUserByEmailTest() throws Exception
    {
        //given
        FindPasswordByEmailRequest emailRequest = createFindPasswordEmail();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any())).willReturn(false);

        //when
        CustomException expectedException =
                (CustomException)catchException(()->sut.findPasswordByEmail(emailRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.NOT_EXIST_USER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NOT_EXIST_USER.getMessage());

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
    }


    private FindPasswordByEmailRequest createFindPasswordEmail() {
        return FindPasswordByEmailRequest.of("hond", "testId", "test@email.com");
    }
    private FindPasswordBySmsRequest createFindPasswordSmsRequest() {
        return FindPasswordBySmsRequest.of("hong", "testId", "+8612345678");
    }


    private VerificationRequest createEmailVerificationRequest() {
        return VerificationRequest.of("test@email.com", "123456");
    }

    private FindUserIdByEmailRequest createFindIdEmailRequest() {
        return FindUserIdByEmailRequest.of("hong", "test@email.com");
    }

    private VerificationRequest createSmsVerificationRequest(String verificationCode) {
        return VerificationRequest.of("01012345678", verificationCode);
    }


    private FindUserIdBySmsRequest createFindUserIdSmsRequest() {
        return FindUserIdBySmsRequest.of("hong", "+8612345678");
    }

    private LoginRequest createLoginRequest() {
        return LoginRequest.of("testId", "abc1234");
    }

    private TokenDto createTokenDto() {
        return TokenDto.of("accessToken", "refreshToken", "testId");
    }

    private Authentication createUsernamePasswordAuthenticationToken() {

        return new UsernamePasswordAuthenticationToken(
                "testId",
                "abc1234",
                Collections.singleton(new SimpleGrantedAuthority(Role.USER.name())));
    }
}