package com.hit.joonggonara.service.user;

import com.hit.joonggonara.custom.login.CustomUserProvider;
import com.hit.joonggonara.dto.TokenDto;
import com.hit.joonggonara.dto.request.*;
import com.hit.joonggonara.dto.response.TokenResponse;
import com.hit.joonggonara.error.CustomException;
import com.hit.joonggonara.error.errorCode.UserErrorCode;
import com.hit.joonggonara.properties.EmailProperties;
import com.hit.joonggonara.properties.RedisProperties;
import com.hit.joonggonara.util.EmailUtil;
import com.hit.joonggonara.util.JwtUtil;
import com.hit.joonggonara.type.Role;
import com.hit.joonggonara.util.RedisUtil;
import com.hit.joonggonara.util.TwilioUtil;
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
        then(redisUtil).should().save(any(), any(), any());
    }
    
    @Test
    @DisplayName("[Service] 존재 하지 않은 권한 정보 일 경우 NOT_EXIST_AUTHORIZATION Exception 발생")
    void notExistAuthorization() throws Exception
    {
        //given
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("test@naver.com", "abc1234");
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
    @DisplayName("[Service] 이미 존재 하는 일 경우 ALREADY_LOGGED_IN_USER Exception 발생")
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
    @DisplayName("[Service] 인증 번호 생성 후 레디스에 저장")
    void receivingVerificationCodeTest() throws Exception
    {
        //given
        PhoneNumberRequest phoneNumberRequest = createPhoneNumberRequest();
        given(twilioUtil.sendMessage(any())).willReturn(Optional.of("12345678"));

        //when
        boolean expectedValue = sut.checkPhoneNumber(phoneNumberRequest);
        //then
        assertThat(expectedValue).isTrue();

        then(redisUtil).should().save(any(),any(),any());
        then(twilioUtil).should().sendMessage(any());
    }

    @Test
    @DisplayName("[Service] 인증번호가 null일 때 NO_RANDOM_NUMBER Excecption 발생")
    void verificationCodeExceptionTest() throws Exception
    {
        //given
        PhoneNumberRequest phoneNumberRequest = createPhoneNumberRequest();
        given(twilioUtil.sendMessage(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException =
                (CustomException)catchException(()->sut.checkPhoneNumber(phoneNumberRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.NO_RANDOM_NUMBER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NO_RANDOM_NUMBER.getMessage());


        then(twilioUtil).should().sendMessage(any());
    }
    
    @Test
    @DisplayName("[Service] 정확한 인증 코드 일 때 true를 리턴")
    void smsVerificationCodeSuccessTest() throws Exception
    {
        //given
        SmsVerificationRequest smsVerificationRequest = createSmsVerificationRequest("123456");
        String verificationCode = "123456";
        given(redisUtil.get(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.checkSmsVerificationCode(smsVerificationRequest);

        //then
        assertThat(expectedValue).isTrue();

        then(redisUtil).should().get(any());

    }
    @Test
    @DisplayName("[Service] 인증 코드가 다를 때 false를 리턴")
    void differentSmsVerificationCodeTest() throws Exception
    {
        //given
        String verificationCode = "123456";
        SmsVerificationRequest smsVerificationRequest = createSmsVerificationRequest("456789");
        given(redisUtil.get(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.checkSmsVerificationCode(smsVerificationRequest);

        //then
        assertThat(expectedValue).isFalse();

        then(redisUtil).should().get(any());

    }

    @Test
    @DisplayName("[Service] 레디스에 인증코드가 존재하지 않을때 No_VERIFICATION_CODE Exception 발생")
    void noSmsVerificationCodeTest() throws Exception
    {
        //given
        SmsVerificationRequest smsVerificationRequest = createSmsVerificationRequest("123456");
        given(redisUtil.get(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException = (CustomException) catchException(() -> sut.checkSmsVerificationCode(smsVerificationRequest));

        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.NO_VERIFICATION_CODE.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NO_VERIFICATION_CODE.getMessage());

        then(redisUtil).should().get(any());
    }
    
    @Test
    @DisplayName("[Service] 이메일 인증코드 발급 후 레디스에 저장 한 후 true를 리턴")
    void emailVerificationCodeSuccessTest() throws Exception
    {
        //given
        String verificationCode = "123456";
        EmailRequest emailRequest = createEmailRequest();
        given(emailUtil.createMessage(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.checkEmail(emailRequest);
        //then
        assertThat(expectedValue).isTrue();

        then(emailUtil).should().createMessage(any());
        then(redisUtil).should().save(any(), any(), any());
    }

    @Test
    @DisplayName("[Service] 인증 코드가 null 일 때 NO_VERIFICATION_CODE Exception 발생")
    void nullEmailVerificationCodeTest() throws Exception
    {
        //given
        EmailRequest emailRequest = createEmailRequest();
        given(emailUtil.createMessage(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException = (CustomException) catchException(() -> sut.checkEmail(emailRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.NO_VERIFICATION_CODE.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.NO_VERIFICATION_CODE.getMessage());

        then(emailUtil).should().createMessage(any());
    }

    @Test
    @DisplayName("[Service] 정확한 인증 코드 일 때 true를 리턴")
    void checkEmailVerificationCodeSuccessTest() throws Exception
    {
        //given
        String verificationCode = "123456";
        EmailVerificationRequest emailVerificationRequest =
                createEmailVerificationRequest();
        given(redisUtil.get(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.checkEmailVerificationCode(emailVerificationRequest);
        //then
        assertThat(expectedValue).isTrue();

        then(redisUtil).should().get(any());
    }

    @Test
    @DisplayName("[Service] 레디스에 인증코드가 존재하지 않을때 No_VERIFICATION_CODE Exception 발생")
    void noEmailVerificationCodeInRedisTest() throws Exception
    {
        //given
        EmailVerificationRequest emailVerificationRequest =
                createEmailVerificationRequest();
        given(redisUtil.get(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException =
                (CustomException) catchException(() -> sut.checkEmailVerificationCode(emailVerificationRequest));
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
        EmailVerificationRequest emailVerificationRequest =
                createEmailVerificationRequest();
        given(redisUtil.get(any())).willReturn(Optional.of(verificationCode));
        //when
        boolean expectedValue = sut.checkEmailVerificationCode(emailVerificationRequest);
        //then
        assertThat(expectedValue).isFalse();

        then(redisUtil).should().get(any());
    }

    private EmailVerificationRequest createEmailVerificationRequest() {
        return EmailVerificationRequest.of("test@email.com", "123456");
    }

    private EmailRequest createEmailRequest() {
        return EmailRequest.of("test@email.com");
    }

    private SmsVerificationRequest createSmsVerificationRequest(String verificationCode) {
        return SmsVerificationRequest.of("01012345678", verificationCode);
    }


    private PhoneNumberRequest createPhoneNumberRequest() {
        return PhoneNumberRequest.of("12345678");
    }

    private LoginRequest createLoginRequest() {
        return LoginRequest.of("test@naver.com", "abc1234");
    }

    private TokenDto createTokenDto() {
        return TokenDto.of("accessToken", "refreshToken", "test@naver.com");
    }

    private Authentication createUsernamePasswordAuthenticationToken() {

        return new UsernamePasswordAuthenticationToken(
                "test@naver.com",
                "abc1234",
                Collections.singleton(new SimpleGrantedAuthority(Role.USER.name())));
    }
}