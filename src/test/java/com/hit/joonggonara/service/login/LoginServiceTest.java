package com.hit.joonggonara.service.login;

import com.hit.joonggonara.common.custom.login.CustomUserProvider;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.BaseErrorCode;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.properties.secretConfig.KakaoSecurityConfig;
import com.hit.joonggonara.common.properties.secretConfig.NaverSecurityConfig;
import com.hit.joonggonara.common.type.AuthenticationType;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.common.type.VerificationType;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.common.util.RedisUtil;
import com.hit.joonggonara.dto.login.OAuth2TokenDto;
import com.hit.joonggonara.dto.login.TokenDto;
import com.hit.joonggonara.dto.request.login.*;
import com.hit.joonggonara.dto.response.login.FindUserIdResponse;
import com.hit.joonggonara.dto.response.login.OAuth2UserDto;
import com.hit.joonggonara.dto.response.login.TokenResponse;
import com.hit.joonggonara.repository.login.MemberRepository;
import com.hit.joonggonara.service.login.oidc.OidcService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static com.hit.joonggonara.common.properties.JwtProperties.AUTHORIZATION;
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
    private VerificationService verificationService;
    @Mock
    private OAuth2Service oAuth2Service;
    @Mock
    private OidcService oidcService;
    @Mock
    private KakaoSecurityConfig kakaoSecurityConfig;
    @Mock
    private NaverSecurityConfig naverSecurityConfig;

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
        given(jwtUtil.createToken(any(), any(), any())).willReturn(tokenDto);
        given(redisUtil.get(any())).willReturn(Optional.empty());
        //when
        TokenResponse expectedTokenResponse = sut.login(loginRequest);
        //then
        assertThat(expectedTokenResponse.accessToken()).isEqualTo(tokenDto.accessToken());
        assertThat(expectedTokenResponse.refreshToken()).isEqualTo(tokenDto.refreshToken());
       

        then(userProvider).should().authenticate(any());
        then(jwtUtil).should().createToken(any(),any(),any());
        then(redisUtil).should().get(any());
        then(redisUtil).should().save(any(), any(), any());
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
        given(jwtUtil.createToken(any(), any(), any())).willReturn(tokenDto);
        given(redisUtil.get(any())).willReturn(Optional.of(refreshToken));
        //when
       CustomException expectedException =
               (CustomException)catchRuntimeException(()-> sut.login(loginRequest));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.ALREADY_LOGGED_IN_USER.getHttpStatus());
        assertThat(expectedException.getMessage()).isEqualTo(UserErrorCode.ALREADY_LOGGED_IN_USER.getMessage());

        then(userProvider).should().authenticate(any());
        then(jwtUtil).should().createToken(any(),any(),any());
        then(redisUtil).should().get(any());
    }
    
    @Test
    @DisplayName("[Service][OAuth2][카카오] OAuth2 로그인이 정상적으로 되고 이미 회원가입된 유저일 경우 토큰과 이메일, 회원가입 상태를 Response로 반환")
    void shouldReturnKakaoOAuth2UserResponseIfAlreadySignedUpUserWhenOAuth2LoginIsSuccessful() throws Exception
    {
        //given
        String code = "test-code";
        OAuth2TokenDto OAuth2TokenDto = createKakaoOAuth2TokenDto();
        String email = "test@email.com";
        TokenDto tokenDto = createTokenDto();
        LoginType loginType = LoginType.KAKAO;
        given(oAuth2Service.requestAccessToken(any(), any())).willReturn(OAuth2TokenDto);
        given(oidcService.getUserInfoFromIdToken(any(), any())).willReturn(email);
        given(memberRepository.existByEmail(any())).willReturn(true);
        given(jwtUtil.createToken(any(), any(), any())).willReturn(tokenDto);
        //when
        OAuth2UserDto oAuth2UserDto = sut.oAuth2Login(code, loginType);
        //then
        assertThat(oAuth2UserDto.accessToken()).isEqualTo(tokenDto.accessToken());
        assertThat(oAuth2UserDto.refreshToken()).isEqualTo(tokenDto.refreshToken());
        assertThat(oAuth2UserDto.principal()).isEqualTo(tokenDto.principal());
        assertThat(oAuth2UserDto.signUpStatus()).isEqualTo(true);
        then(oAuth2Service).should().requestAccessToken(any(), any());
        then(oidcService).should().getUserInfoFromIdToken(any(), any());
        then(memberRepository).should().existByEmail(any());
        then(jwtUtil).should().createToken(any(), any(), any());
        then(redisUtil).should().save(any(), any(), any());
    }

    @Test
    @DisplayName("[Service][OAuth2][카카오] OAuth2 로그인이 정상적으로 되고 회원가입이 되어 있지 않은 유저일 경우 이메일과 회원가입 상태를 Response로 반환")
    void shouldReturnKakaoOAuth2UserResponseIfNotAlreadySignUpUserWhenOAuth2LoginIsSuccessful() throws Exception
    {
        //given
        String code = "test-code";
        OAuth2TokenDto OAuth2TokenDto = createKakaoOAuth2TokenDto();
        String email = "test@email.com";
        LoginType loginType = LoginType.KAKAO;
        given(oAuth2Service.requestAccessToken(any(), any())).willReturn(OAuth2TokenDto);
        given(oidcService.getUserInfoFromIdToken(any(), any())).willReturn(email);
        given(memberRepository.existByEmail(any())).willReturn(false);
        //when
        OAuth2UserDto oAuth2UserDto = sut.oAuth2Login(code, loginType);
        //then
        assertThat(oAuth2UserDto.accessToken()).isNull();
        assertThat(oAuth2UserDto.refreshToken()).isNull();
        assertThat(oAuth2UserDto.principal()).isEqualTo(email);
        assertThat(oAuth2UserDto.signUpStatus()).isEqualTo(false);
        then(oAuth2Service).should().requestAccessToken(any(), any());
        then(oidcService).should().getUserInfoFromIdToken(any(), any());
        then(memberRepository).should().existByEmail(any());
    }

    @Test
    @DisplayName("[Service][OAuth2][네이버] OAuth2 로그인이 정상적으로 되고 이미 회원가입된 유저일 경우 토큰과 이메일, 회원가입 상태를 Response로 반환")
    void shouldReturnNaverOAuth2UserResponseIfAlreadySignedUpUserWhenOAuth2LoginIsSuccessful() throws Exception
    {
        //given
        String code = "test-code";
        OAuth2TokenDto OAuth2TokenDto = createKakaoOAuth2TokenDto();
        String email = "test@email.com";
        TokenDto tokenDto = createTokenDto();
        LoginType loginType = LoginType.NAVER;
        given(oAuth2Service.requestAccessToken(any(), any())).willReturn(OAuth2TokenDto);
        given(oAuth2Service.getUserInfoFromAccessToken(any(), any())).willReturn(email);
        given(memberRepository.existByEmail(any())).willReturn(true);
        given(jwtUtil.createToken(any(), any(), any())).willReturn(tokenDto);
        //when
        OAuth2UserDto oAuth2UserDto = sut.oAuth2Login(code, loginType);
        //then
        assertThat(oAuth2UserDto.accessToken()).isEqualTo(tokenDto.accessToken());
        assertThat(oAuth2UserDto.refreshToken()).isEqualTo(tokenDto.refreshToken());
        assertThat(oAuth2UserDto.principal()).isEqualTo(tokenDto.principal());
        assertThat(oAuth2UserDto.signUpStatus()).isEqualTo(true);
        then(oAuth2Service).should().requestAccessToken(any(), any());
        then(oAuth2Service).should().getUserInfoFromAccessToken(any(), any());
        then(memberRepository).should().existByEmail(any());
        then(jwtUtil).should().createToken(any(), any(), any());
        then(redisUtil).should().save(any(), any(), any());
    }

    @Test
    @DisplayName("[Service][OAuth2][네이버] OAuth2 로그인이 정상적으로 되고 회원가입이 되어 있지 않은 유저일 경우 이메일과 회원가입 상태를 Response로 반환")
    void shouldReturnNaverOAuth2UserResponseIfNotAlreadySignUpUserWhenOAuth2LoginIsSuccessful() throws Exception
    {
        //given
        String code = "test-code";
        OAuth2TokenDto OAuth2TokenDto = createKakaoOAuth2TokenDto();
        String email = "test@email.com";
        LoginType loginType = LoginType.NAVER;
        given(oAuth2Service.requestAccessToken(any(), any())).willReturn(OAuth2TokenDto);
        given(oAuth2Service.getUserInfoFromAccessToken(any(), any())).willReturn(email);
        given(memberRepository.existByEmail(any())).willReturn(false);
        //when
        OAuth2UserDto oAuth2UserDto = sut.oAuth2Login(code, loginType);
        //then
        assertThat(oAuth2UserDto.accessToken()).isNull();
        assertThat(oAuth2UserDto.refreshToken()).isNull();
        assertThat(oAuth2UserDto.principal()).isEqualTo(email);
        assertThat(oAuth2UserDto.signUpStatus()).isEqualTo(false);
        then(oAuth2Service).should().requestAccessToken(any(), any());
        then(oAuth2Service).should().getUserInfoFromAccessToken(any(), any());
        then(memberRepository).should().existByEmail(any());
    }


    private OAuth2TokenDto createKakaoOAuth2TokenDto() {
        return OAuth2TokenDto.of(
                "access_token",
                "id_token"
        );
    }

    @Test
    @DisplayName("[Service][Sms][아이디 찾기]  회원 정보가 존재하고  핸드폰 인증코드가 정상적으로 발송 및 Redis에 저장 될 경우 true를 리턴")
    void checkExistUserAndReceivingVerificationCodeTest() throws Exception
    {
        //given
        FindUserIdBySmsRequest findUserIdBySmsRequest = createFindUserIdSmsRequest();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any())).willReturn(true);

        //when
        boolean expectedValue = sut.findUserIdBySms(findUserIdBySmsRequest);
        //then
        assertThat(expectedValue).isTrue();

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
        then(verificationService).should().sendSms(any());
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
    @DisplayName("[Service][Email][아이디 찾기]  회원 정보가 존재하고  핸드폰 인증코드가 정상적으로 발송 및 Redis에 저장 될 경우 true를 리턴")
    void checkExistUserAndReceivingEmailVerificationCodeTest() throws Exception
    {
        //given
        FindUserIdByEmailRequest findUserIdByEmailRequest = createFindIdEmailRequest();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any())).willReturn(true);

        //when
        boolean expectedValue = sut.findUserIdByEmail(findUserIdByEmailRequest);
        //then
        assertThat(expectedValue).isTrue();

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
        then(verificationService).should().sendEmail(any());
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
    @DisplayName("[Service][Sms][패스워드 찾기] 회원 정보가 존재하고  핸드폰 인증코드가 정상적으로 발송 및 Redis에 저장 될 경우 true를 리턴")
    void findPassword_checkExistUserAndReceivingVerificationCodeTest() throws Exception
    {
        //given
        FindPasswordBySmsRequest findPasswordBySmsRequest = createFindPasswordSmsRequest();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any())).willReturn(true);

        //when
        boolean expectedValue = sut.findPasswordBySms(findPasswordBySmsRequest);
        //then
        assertThat(expectedValue).isTrue();

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
        then(verificationService).should().sendSms(any());
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
        FindPasswordByEmailRequest emailRequest = createFindPasswordEmail();
        given(memberRepository.existByUserNameAndVerificationTypeValue(any(), any()))
                .willReturn(true);
        //when
        boolean expectedValue = sut.findPasswordByEmail(emailRequest);
        //then
        assertThat(expectedValue).isTrue();

        then(memberRepository).should().existByUserNameAndVerificationTypeValue(any(), any());
        then(verificationService).should().sendEmail(any());
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

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Service][인증 코드 검사][ID] 인증 코드가 일치할 경우 UserId Response를 리턴")
    void returnUserIdResponseWhenIfTheVerificationCodeMatch(VerificationType type) throws Exception
    {
        //given
        String userId = "testId";
        VerificationRequest verificationRequest = createVerificationCodeRequest();
        given(verificationService.checkVerificationCode(any(), any())).willReturn(true);
        given(memberRepository.findUserIdOrPasswordByPhoneNumberOrEmail(any())).willReturn(Optional.of(userId));
        //when
        FindUserIdResponse expectedResponse = sut.checkUserIdVerificationCode(verificationRequest, type);
        //then
        assertThat(expectedResponse.userId()).isEqualTo(userId);
        then(verificationService).should().checkVerificationCode(any(), any());
        then(memberRepository).should().findUserIdOrPasswordByPhoneNumberOrEmail(any());
    }

    static Stream<Arguments> returnUserIdResponseWhenIfTheVerificationCodeMatch(){
        return Stream.of(
                Arguments.of(VerificationType.SMS),
                Arguments.of(VerificationType.EMAIL)
        );
    }


    @Test
    @DisplayName("[Service][인증 코드 검사][ID] 인증 타입에 오류로 INTERNAL_SERVER_ERROR 에러 발생")
    void throwExceptionWhenErrorVerificationCode() throws Exception
    {
        //given
        VerificationRequest verificationRequest = createVerificationCodeRequest();
        //when
        CustomException expectedException =
                (CustomException)catchException(()->sut.checkUserIdVerificationCode(verificationRequest, VerificationType.ID_EMAIL));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(BaseErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus());
        assertThat(expectedException).hasMessage(BaseErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Service][인증 코드 검사][Exception] 인증 코드가 불일치 할 경우 VERIFICATION_CODE_MISMATCH 에러 발생")
    void throwExceptionWhenTheVerificationNotMatch(VerificationType type) throws Exception
    {
        //given
        VerificationRequest verificationRequest = createVerificationCodeRequest();
        given(verificationService.checkVerificationCode(any(), any())).willReturn(false);
        //when
        CustomException expectedException =
                (CustomException)catchException(()->sut.checkUserIdVerificationCode(verificationRequest, type));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.VERIFICATION_CODE_MISMATCH.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.VERIFICATION_CODE_MISMATCH.getMessage());
        then(verificationService).should().checkVerificationCode(any(), any());
    }
    static Stream<Arguments> throwExceptionWhenTheVerificationNotMatch(){
        return Stream.of(
                Arguments.of(VerificationType.SMS),
                Arguments.of(VerificationType.EMAIL)
        );
    }
    @MethodSource
    @ParameterizedTest
    @DisplayName("[Service][인증 코드 검사][Exception] 인증 코드가 일치 하지만 회원 조회가 되지 않을 경우 UserNotFound 에러 발생")
    void throwExceptionWhenUserNotFound(VerificationType type) throws Exception
    {
        //given
        VerificationRequest verificationRequest = createVerificationCodeRequest();
        AuthenticationType authenticationType = AuthenticationType.ID;
        given(verificationService.checkVerificationCode(any(), any())).willReturn(true);
        given(memberRepository.findUserIdOrPasswordByPhoneNumberOrEmail(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException =
                (CustomException)catchException(()->sut.checkUserIdVerificationCode(verificationRequest, type));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus()).isEqualTo(UserErrorCode.USER_NOT_FOUND.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        then(verificationService).should().checkVerificationCode(any(), any());
        then(memberRepository).should().findUserIdOrPasswordByPhoneNumberOrEmail(any());
    }

    static Stream<Arguments> throwExceptionWhenUserNotFound(){
        return Stream.of(
                Arguments.of(VerificationType.SMS),
                Arguments.of(VerificationType.EMAIL)
        );
    }
    @MethodSource
    @ParameterizedTest
    @DisplayName("[Service][인증 코드 검사][Password] 인증 코드가 일치할 경우 Password를 리턴")
    void returnPasswordWhenIfTheVerificationCodeMatch(VerificationType type) throws Exception
    {
        //given
        String password = "Abc1234*";
        VerificationRequest verificationRequest = createVerificationCodeRequest();
        given(verificationService.checkVerificationCode(any(), any())).willReturn(true);
        given(memberRepository.findUserIdOrPasswordByPhoneNumberOrEmail(any())).willReturn(Optional.of(password));
        //when
        boolean expectedValue = sut.checkPasswordVerificationCode(verificationRequest, type);
        //then
        assertThat(expectedValue).isTrue();
        then(verificationService).should().checkVerificationCode(any(), any());
        then(memberRepository).should().findUserIdOrPasswordByPhoneNumberOrEmail(any());
    }

    static Stream<Arguments> returnPasswordWhenIfTheVerificationCodeMatch(){
        return Stream.of(
                Arguments.of(VerificationType.SMS),
                Arguments.of(VerificationType.EMAIL)
        );
    }




    @Test
    @DisplayName("[Service][토큰 재발급] RefreshToken을 통해 정상적으로 AccessToken과 RefreshToken을 재발급 받으면 TokenDto를 통해 반환")
    void reissueTest() throws Exception
    {
        //given
        TokenDto tokenDto = createTokenDto();
        String token = "test_token";
        given(jwtUtil.getPrincipal(any())).willReturn("testId");
        given(jwtUtil.getRole(any())).willReturn(Role.ROLE_USER);
        given(redisUtil.get(any())).willReturn(Optional.of("Refresh Token"));
        given(jwtUtil.createToken(any(), any(), any())).willReturn(tokenDto);
        //when
        TokenResponse expectedResponse = sut.reissueToken(token);
        //then

        assertThat(expectedResponse).isNotNull();
        assertThat(expectedResponse.accessToken()).isEqualTo(tokenDto.accessToken());
        assertThat(expectedResponse.refreshToken()).isEqualTo(tokenDto.refreshToken());

        then(jwtUtil).should().getPrincipal(any());
        then(jwtUtil).should().getRole(any());
        then(redisUtil).should().get(any());
        then(jwtUtil).should().createToken(any(), any(), any());
        then(redisUtil).should().delete(any());
        then(redisUtil).should().save(any(), any(), any());
    }

    @Test
    @DisplayName("[Service][토큰 재발급] RefreshToken이 null 일 경우 ALREADY_LOGGED_OUT_USER 에러 발생")
    void reissueRefreshTokenNullTest() throws Exception
    {
        //given
        String token = "test_token";
        //when
        CustomException expectedException =
                (CustomException)catchException(()->sut.reissueToken(token));
        //then

        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.ALREADY_LOGGED_OUT_USER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.ALREADY_LOGGED_OUT_USER.getMessage());

    }

    @Test
    @DisplayName("[Service][토큰 재발급] RefreshToken이 Redis에 없을 경우 ALREADY_LOGGED_OUT_USER 에러 발생")
    void reissueNotExistRefreshTokenInRedisTest() throws Exception
    {
        //give
        String token = "test_token";
        given(jwtUtil.getPrincipal(any())).willReturn("testId");
        given(jwtUtil.getRole(any())).willReturn(Role.ROLE_USER);
        given(redisUtil.get(any())).willReturn(Optional.empty());

        //when
        CustomException expectedException =
                (CustomException)catchException(()->sut.reissueToken(token));
        //then

        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.ALREADY_LOGGED_OUT_USER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.ALREADY_LOGGED_OUT_USER.getMessage());

        then(jwtUtil).should().getPrincipal(any());
        then(jwtUtil).should().getRole(any());
        then(redisUtil).should().get(any());
    }

    @Test
    @DisplayName("[Service][회원 탈퇴][기본 로그인] 회원 탈퇴가 성공 적으로 될 경우 true를 리턴한다.")
    void withdrawalGeneralLoginTest() throws Exception
    {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, JwtProperties.JWT_TYPE + "accessToken");

        given(jwtUtil.getPrincipal(any())).willReturn("principal");
        given(jwtUtil.getLoginType(any())).willReturn(LoginType.GENERAL);
        //when
        boolean expectedValue = sut.withdrawal(request);
        //then
        assertThat(expectedValue).isTrue();

        then(jwtUtil).should().getPrincipal(any());
        then(jwtUtil).should().getLoginType(any());
        then(memberRepository).should().deleteByUserId(any());
        then(redisUtil).should().delete(any());
    }
    @Test
    @DisplayName("[Service][회원 탈퇴][기본 로그인] 회원 탈퇴가 성공 적으로 될 경우 true를 리턴한다.")
    void withdrawalOAuth2LoginTest() throws Exception
    {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, JwtProperties.JWT_TYPE + "accessToken");

        given(jwtUtil.getPrincipal(any())).willReturn("principal");
        given(jwtUtil.getLoginType(any())).willReturn(LoginType.NAVER);
        //when
        boolean expectedValue = sut.withdrawal(request);
        //then
        assertThat(expectedValue).isTrue();

        then(jwtUtil).should().getPrincipal(any());
        then(jwtUtil).should().getLoginType(any());
        then(memberRepository).should().deleteByEmail(any());
        then(redisUtil).should().delete(any());
    }

    @Test
    @DisplayName("[Service][회원 탈퇴] Request Header에 AccessToken이 존재하지 않을 경우 ALREADY_LOGGED_OUT_USER 에러 발생")
    void withdrawalNoAccessTokenInRequestHeaderTest() throws Exception
    {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        CustomException expectedException =
                (CustomException) catchException(()->sut.withdrawal(request));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.ALREADY_LOGGED_OUT_USER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.ALREADY_LOGGED_OUT_USER.getMessage());

    }
    private VerificationRequest createVerificationCodeRequest() {
        return VerificationRequest.of("+8612345678", "123456");
    }

    private FindPasswordByEmailRequest createFindPasswordEmail() {
        return FindPasswordByEmailRequest.of("hong", "testId", "test@principal.com");
    }
    private FindPasswordBySmsRequest createFindPasswordSmsRequest() {
        return FindPasswordBySmsRequest.of("hong", "testId", "+8612345678");
    }

    private FindUserIdByEmailRequest createFindIdEmailRequest() {
        return FindUserIdByEmailRequest.of("hong", "test@principal.com");
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
                Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.name())));
    }
}