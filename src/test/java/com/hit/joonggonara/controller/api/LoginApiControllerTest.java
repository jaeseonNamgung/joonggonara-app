package com.hit.joonggonara.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.ErrorCode;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.util.CookieUtil;
import com.hit.joonggonara.dto.request.login.*;
import com.hit.joonggonara.dto.response.product.MemberResponse;
import com.hit.joonggonara.dto.response.login.FindUserIdResponse;
import com.hit.joonggonara.dto.response.login.MemberTokenResponse;
import com.hit.joonggonara.dto.response.login.OAuth2UserDto;
import com.hit.joonggonara.dto.response.login.TokenResponse;
import com.hit.joonggonara.service.login.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.hit.joonggonara.common.properties.JwtProperties.*;
import static com.hit.joonggonara.common.properties.ValidationMessageProperties.*;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoginApiController.class)
class LoginApiControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CookieUtil cookieUtil;
    @MockBean
    private LoginService loginService;

    @BeforeEach
    public void setup() {
        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(0);
            String name = invocation.getArgument(1);
            String value = invocation.getArgument(2);
            Cookie cookie = new Cookie(name, value);
            response.addCookie(cookie);
            return null;
        }).when(cookieUtil).addCookie(any(HttpServletResponse.class), anyString(), anyString());
    }

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][POST] login이 성공적으로 될 경우 accessToken과 refreshToken을 각각 Header와 Cookie에 저장 후 true를 반환")
    void loginSuccessTest() throws Exception
    {
        //given
        LoginRequest loginRequest = createLoginRequest();
        MemberTokenResponse memberTokenResponse = createMemberTokenResponse();

        given(loginService.login(any())).willReturn(memberTokenResponse);
        //when & then
        mvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequest))
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(header().string(AUTHORIZATION, JWT_TYPE + "accessToken"))
                .andExpect(cookie().value(REFRESH_TOKEN_NAME, "refreshToken"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.nickName").value("nickName"))
                .andExpect(jsonPath("$.loginType").value(LoginType.GENERAL.name()));
        then(loginService).should().login(any());
        then(cookieUtil).should().addCookie(any(HttpServletResponse.class), any(), any());
    }

    private MemberTokenResponse createMemberTokenResponse() {
        return MemberTokenResponse.of(MemberResponse.of(
                1L,
                "userId",
                "test@email.com",
                "name",
                "nickName",
                null,
                "01012345678",
                LoginType.GENERAL
        ), "accessToken" , "refreshToken");
    }

    private MemberTokenResponse createSocialMemberTokenResponse() {
        return MemberTokenResponse.of(MemberResponse.of(
                1L,
                null,
                "test@email.com",
                "name",
                "nickName",
                "profile",
                "01012345678",
                LoginType.KAKAO
        ), "accessToken" , "refreshToken");
    }


    @WithMockUser(roles = "GUEST")
    @MethodSource
    @ParameterizedTest
    @DisplayName("Validation 검증 오류 ApiException 에서 테스트 ")
    void validationAPiExceptionTest(String email, String password, List<String> field, List<String> message) throws Exception
    {
        //given
        LoginRequest loginRequest = LoginRequest.of(email, password);
        //when
        ResultActions resultActions = mvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf())
                ).andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.httpStatus").value(400));

        for (int i = 0; i < field.size(); i++) {
            String expectedField = field.get(i);
            String expectedMessage = message.get(i);
            resultActions
                    .andExpect(jsonPath("$.fieldErrors[*].field", hasItem(expectedField)))
                    .andExpect(jsonPath("$.fieldErrors[*].message", hasItem(expectedMessage)));
        }
        //then
    }
    static Stream<Arguments> validationAPiExceptionTest(){
        return Stream.of(
                Arguments.of("", "abc1234*", List.of("userId"), List.of(ID_PASSWORD_NOT_BLANK)),
                Arguments.of(" ", "abc1234*", List.of("userId"), List.of(ID_PASSWORD_NOT_BLANK)),
                Arguments.of("testId", " ", List.of("password"), List.of(ID_PASSWORD_NOT_BLANK)),
                Arguments.of(null, "abc1234*", List.of("userId"), List.of(ID_PASSWORD_NOT_BLANK)),
                Arguments.of("testId", null, List.of("password"), List.of(ID_PASSWORD_NOT_BLANK)),
                Arguments.of("", "", List.of("userId", "password"), List.of(ID_PASSWORD_NOT_BLANK, ID_PASSWORD_NOT_BLANK)),
                Arguments.of(null, null,List.of("userId", "password"), List.of(ID_PASSWORD_NOT_BLANK, ID_PASSWORD_NOT_BLANK))
        );
    }


    @WithMockUser(roles = "GUEST")
    @MethodSource
    @ParameterizedTest
    @DisplayName("[API][Exception]  ApiException 예의 처리 테스트")
    void apiExceptionTest(ErrorCode errorCode) throws Exception
    {
        //given
        given(loginService.login(any())).willThrow(new CustomException(errorCode));
        LoginRequest loginRequest = createLoginRequest();
        //when & then
        mvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequest))
                .with(csrf())
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.httpStatus").value(errorCode.getHttpStatus().value()))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()));
        then(loginService).should().login(any());

    }

    static Stream<Arguments> apiExceptionTest(){
        return Stream.of(
                Arguments.of(UserErrorCode.NOT_EXIST_AUTHORIZATION),
                Arguments.of(UserErrorCode.ALREADY_LOGGED_IN_USER)
        );
    }

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][GET][소셜 로그인] 이미 가입된 회원이고 성공적으로 로그인 될 경우 AccessToken과 RefreshToken을 Header와 Cookie에 저장 후 " +
            "ResponseEntity에 OAUth2UserResponse Body를 반환")
    void ShouldReturnOAUth2UserResponseWhenAlreadySignedUpAndLoginIsSuccessFull() throws Exception
    {
        //given
        MemberTokenResponse memberTokenResponse = createSocialMemberTokenResponse();
        given(loginService.oAuth2Login(any(), any())).willReturn(memberTokenResponse);
        //when & then
        mvc.perform(get("/user/login/oauth2/code/kakao")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .queryParam("code", "test-code")
                        .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string(AUTHORIZATION, JWT_TYPE + "accessToken"))
                .andExpect(cookie().value(REFRESH_TOKEN_NAME, "refreshToken"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.loginType").value(LoginType.KAKAO.name()));

        then(loginService).should().oAuth2Login(any(), any());
        then(cookieUtil).should().addCookie(any(HttpServletResponse.class), any(), any());
    }

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][GET][소셜 로그인] 가입되어 있지 않은 회원이면 ResponseEntity에 OAUth2UserResponse에 signUpStatus에 false를 넣고 반환")
    void IfNotSignUpUserTest() throws Exception
    {
        //given
        MemberTokenResponse memberTokenResponse = createSocialMemberTokenResponse();
        given(loginService.oAuth2Login(any(), any())).willReturn(memberTokenResponse);
        //when & then
        mvc.perform(get("/user/login/oauth2/code/kakao")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .queryParam("code", "test-code")
                        .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.profile").value("profile"))
                .andExpect(jsonPath("$.loginType").value(LoginType.KAKAO.name()));

        then(loginService).should().oAuth2Login(any(), any());
    }

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][POST] SMS 인증 코드 요청 - 아이디 찾기")
    void findUserIdBySmsTest() throws Exception
    {
        //given
        FindUserIdBySmsRequest findUserIdBySmsRequest =
                FindUserIdBySmsRequest.of("hong", "+8617512345678");
        given(loginService.findUserIdBySms(any())).willReturn(true);
        //when & then
        mvc.perform(post("/user/login/findId/sms")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(findUserIdBySmsRequest))
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));

        then(loginService).should().findUserIdBySms(any());
    }

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][POST] Email 인증 코드 요청 - 아이디 찾기")
    void findUserIdByEmailTest() throws Exception
    {
        //given
        FindUserIdByEmailRequest findUserIdByEmailRequest = FindUserIdByEmailRequest.of("hong", "test@principal.com");
        given(loginService.findUserIdByEmail(any())).willReturn(true);
        //when & then
        mvc.perform(post("/user/login/findId/email")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(findUserIdByEmailRequest))
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));

        then(loginService).should().findUserIdByEmail(any());
    }

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][POST] SMS 인증 코드 요청 - 비밀번호 찾기")
    void findPasswordBySmsTest() throws Exception
    {
        //given
        FindPasswordBySmsRequest findPasswordBySmsRequest =
                FindPasswordBySmsRequest.of("hong", "testId", "+8617512345678");
        given(loginService.findPasswordBySms(any())).willReturn(true);
        //when & then
        mvc.perform(post("/user/login/findPassword/sms")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(findPasswordBySmsRequest))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));

        then(loginService).should().findPasswordBySms(any());
    }

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][POST] Email 인증 코드 요청 - 비밀번호 찾기")
    void findPasswordByEmailTest() throws Exception
    {
        //given
        FindPasswordByEmailRequest findPasswordByEmailRequest =
                FindPasswordByEmailRequest.of("hong", "testId", "test@principal.com");
        given(loginService.findPasswordByEmail(any())).willReturn(true);
        //when & then
        mvc.perform(post("/user/login/findPassword/email")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(findPasswordByEmailRequest))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));

        then(loginService).should().findPasswordByEmail(any());
    }

    @WithMockUser(roles = "GUEST")
    @MethodSource
    @ParameterizedTest
    @DisplayName("[API][POST] Email 또는 Sms 인증 코드 검사 후 아이디 반환")
    void returnUserIdWhenIfSuccessfullyAnEmailOrSms(String verificationType,
                                               String expectedValue,
                                               String verificationKey) throws Exception
    {
        //given
        VerificationRequest verificationRequest =
                VerificationRequest.of(verificationKey, "123456");
        FindUserIdResponse findUserIdResponse = FindUserIdResponse.of("testId");
        given(loginService.checkUserIdVerificationCode(any(), any())).willReturn(findUserIdResponse);
        //when & then
        mvc.perform(post("/user/login/checkVerificationCode/userId")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("verificationType", verificationType)
                .content(objectMapper.writeValueAsString(verificationRequest))
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.userId").value(expectedValue));
        then(loginService).should().checkUserIdVerificationCode(any(), any());
    }

    static Stream<Arguments> returnUserIdWhenIfSuccessfullyAnEmailOrSms(){
        return Stream.of(
                Arguments.of("sms", "testId", "+8612345678"),
                Arguments.of("email", "testId", "test@email.com")
        );
    }

    @WithMockUser(roles = "GUEST")
    @MethodSource
    @ParameterizedTest
    @DisplayName("[API][POST][Password] Email 또는 Sms 인증 코드 검사 후 true를 리턴")
    void returnTrueWhenIfSuccessfullyAnEmailOrSms(String verificationType,
                                               String verificationKey) throws Exception
    {
        //given
        VerificationRequest verificationRequest =
                VerificationRequest.of(verificationKey, "123456");
        FindUserIdResponse findUserIdResponse = FindUserIdResponse.of("testId");
        given(loginService.checkPasswordVerificationCode(any(), any())).willReturn(true);
        //when & then
        mvc.perform(post("/user/login/checkVerificationCode/password")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("verificationType", verificationType)
                        .content(objectMapper.writeValueAsString(verificationRequest))
                        .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));
        then(loginService).should().checkPasswordVerificationCode(any(), any());
    }

    static Stream<Arguments> returnTrueWhenIfSuccessfullyAnEmailOrSms(){
        return Stream.of(
                Arguments.of("sms", "+8612345678"),
                Arguments.of("email", "test@email.com")
        );
    }


    @WithMockUser(roles = "GUEST")
    @MethodSource
    @ParameterizedTest
    @DisplayName("이메일로 아이디 찾기 Validation 검증 오류 ApiException 에서 테스트 ")
    void findIdByEmail_validationAPiExceptionTest(String name, String email, List<String> field, List<String> message) throws Exception
    {
        //given
        FindUserIdByEmailRequest findUserIdByEmailRequest = FindUserIdByEmailRequest.of(name, email);
        //when
        ResultActions resultActions = mvc.perform(post("/user/login/findId/email")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(findUserIdByEmailRequest))
                        .with(csrf())
                ).andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.httpStatus").value(400));

        for (int i = 0; i < field.size(); i++) {
            String expectedField = field.get(i);
            String expectedMessage = message.get(i);
            resultActions
                    .andExpect(jsonPath("$.fieldErrors[*].field", hasItem(expectedField)))
                    .andExpect(jsonPath("$.fieldErrors[*].message", hasItem(expectedMessage)));
        }
        //then
    }
    static Stream<Arguments> findIdByEmail_validationAPiExceptionTest(){
        return Stream.of(
                Arguments.of("", "test@email.com", List.of("name"), List.of(NAME_NOT_BLANK)),
                Arguments.of(" ", "test@email.com", List.of("name"), List.of(NAME_NOT_BLANK)),
                Arguments.of("hong", " ", List.of("email"), List.of(EMAIL_NOT_BLANK)),
                Arguments.of(null, "test@email.com", List.of("name"), List.of(NAME_NOT_BLANK)),
                Arguments.of("hong", null, List.of("email"), List.of(EMAIL_NOT_BLANK)),
                Arguments.of("", "", List.of("name", "email"), List.of(NAME_NOT_BLANK, EMAIL_NOT_BLANK)),
                Arguments.of(null, null,List.of("name", "email"), List.of(NAME_NOT_BLANK, EMAIL_NOT_BLANK)),
                Arguments.of("hong", "test",List.of("email"), List.of(EMAIL))
        );
    }

    @WithMockUser(roles = "USER")
    @Test
    @DisplayName("[API][PUT] Refresh Token 정상적으로 재발급 되면 true를 리턴")
    void refreshTokenTest() throws Exception
    {
        //given
        String refreshToken = "refreshToken";
        Cookie cookie = new MockCookie(JwtProperties.REFRESH_TOKEN_NAME, refreshToken);
        TokenResponse tokenResponse = createTokenResponse();
        given(cookieUtil.getCookie(any())).willReturn(Optional.of(cookie));
        given(loginService.reissueToken(any())).willReturn(tokenResponse);
        //when & then
        mvc.perform(put("/user/login/reissue")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf())
                        .cookie(cookie)
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));

        then(cookieUtil).should().getCookie(any());
        then(loginService).should().reissueToken(any());
        then(cookieUtil).should().addCookie(any(),any(),any());
    }

    @WithMockUser(roles = "USER")
    @Test
    @DisplayName("[API][PUT] Cookie에 값이 없다면 ALREADY_LOGGED_OUT_USER 에러 발생")
    void refreshTokenNoRefreshTokenInCookieTest() throws Exception
    {
        //given
        given(cookieUtil.getCookie(any())).willReturn(Optional.empty());
        //when & then
        mvc.perform(put("/user/login/reissue")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf())
                ).andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.httpStatus")
                        .value(UserErrorCode.ALREADY_LOGGED_OUT_USER.getHttpStatus().value()))
                .andExpect(jsonPath("$.message").value(UserErrorCode.ALREADY_LOGGED_OUT_USER.getMessage()));

        then(cookieUtil).should().getCookie(any());
    }

    @WithMockUser(roles = "USER")
    @Test
    @DisplayName("[API][PUT] 패스워드가 정상적으로 변경될 경우 true를 리턴")
    void returnTrueIfTheUpdatePasswordIsSuccessful() throws Exception {
        //given
        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.of("userId", "newPassword");
        given(loginService.updatePassword(any())).willReturn(true);
        //when & then
        mvc.perform(put("/user/login/update/password")
                .content(objectMapper.writeValueAsString(updatePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));

        then(loginService).should().updatePassword(any());
    }


    private LoginRequest createLoginRequest() {
        return LoginRequest.of("test@naver.com", "abc1234*");
    }

    private TokenResponse createTokenResponse() {
        return TokenResponse.of("accessToken", "refreshToken");
    }


    private OAuth2UserDto createOAuth2UserDto(Boolean signUpStatus) {
        return OAuth2UserDto.of(
                "accessToken",
                "refreshToken",
                "test@email.com",
                "profile",
                signUpStatus);
    }
}
