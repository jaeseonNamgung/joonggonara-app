package com.hit.joonggonara.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.joonggonara.common.config.SecurityConfig;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.ErrorCode;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.dto.request.login.*;
import com.hit.joonggonara.dto.response.login.TokenResponse;
import com.hit.joonggonara.service.login.LoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.Stream;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.*;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoginApiController.class, excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class

),
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class LoginApiControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LoginService loginService;

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API] login 테스트")
    void loginSuccessTest() throws Exception
    {
        //given
        LoginRequest loginRequest = createLoginRequest();
        TokenResponse tokenResponse = createTokenResponse();
        given(loginService.login(any())).willReturn(tokenResponse);
        //when & then
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequest))
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));
        then(loginService).should().login(any());
    }



    @MethodSource
    @ParameterizedTest
    @DisplayName("Validation 검증 오류 ApiException 에서 테스트 ")
    void validationAPiExceptionTest(String email, String password, List<String> field, List<String> message) throws Exception
    {
        //given
        LoginRequest loginRequest = LoginRequest.of(email, password);
        //when
        ResultActions resultActions = mvc.perform(post("/login")
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


    @MethodSource
    @ParameterizedTest
    @DisplayName("[API][Exception]  ApiException 예의 처리 테스트")
    void apiExceptionTest(ErrorCode errorCode) throws Exception
    {
        //given
        given(loginService.login(any())).willThrow(new CustomException(errorCode));
        LoginRequest loginRequest = createLoginRequest();
        //when & then
        mvc.perform(post("/login")
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
    @DisplayName("[API][POST] 핸드폰 번호로 아이디 찾기")
    void findUserIdBySmsTest() throws Exception
    {
        //given
        FindUserIdBySmsRequest findUserIdBySmsRequest =
                FindUserIdBySmsRequest.of("hong", "+8617512345678");
        given(loginService.findUserIdBySms(any())).willReturn(true);
        //when & then
        mvc.perform(post("/login/findId/sms")
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
    @DisplayName("[API][POST] 이메일로 아이디 찾기")
    void findUserIdByEmailTest() throws Exception
    {
        //given
        FindUserIdByEmailRequest findUserIdByEmailRequest = FindUserIdByEmailRequest.of("hong", "test@email.com");
        given(loginService.findUserIdByEmail(any())).willReturn(true);
        //when & then
        mvc.perform(post("/login/findId/email")
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
    @DisplayName("[API][POST] 핸드폰 번호로 패스워드 찾기")
    void findPasswordBySmsTest() throws Exception
    {
        //given
        FindPasswordBySmsRequest findPasswordBySmsRequest =
                FindPasswordBySmsRequest.of("hong", "testId", "+8617512345678");
        given(loginService.findPasswordBySms(any())).willReturn(true);
        //when & then
        mvc.perform(post("/login/findPassword/sms")
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
    @DisplayName("[API][POST] 이메일로 패스워드 찾기")
    void findPasswordByEmailTest() throws Exception
    {
        //given
        FindPasswordByEmailRequest findPasswordByEmailRequest =
                FindPasswordByEmailRequest.of("hong", "testId", "test@email.com");
        given(loginService.findPasswordByEmail(any())).willReturn(true);
        //when & then
        mvc.perform(post("/login/findPassword/email")
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
    @Test
    @DisplayName("[API][POST] 핸드폰 인증 코드 검사 성공")
    void successfulPhoneNumberVerificationTest() throws Exception
    {
        //given
        VerificationRequest verificationRequest =
                VerificationRequest.of("+8617512345678", "123456");
        given(loginService.checkVerificationCode(any(), any())).willReturn(true);
        //when & then
        mvc.perform(post("/login/checkVerificationCode")
                .contentType(MediaType.APPLICATION_JSON_VALUE).param("verificationType", "sms")
                .content(objectMapper.writeValueAsString(verificationRequest))
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));
        then(loginService).should().checkVerificationCode(any(), any());
    }
    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][POST] 이메일 인증 코드 검사 성공")
    void successfulEmailVerificationTest() throws Exception
    {
        //given
        VerificationRequest verificationRequest =
                VerificationRequest.of("test@email.com", "123456");
        given(loginService.checkVerificationCode(any(), any())).willReturn(true);
        //when & then
        mvc.perform(post("/login/checkVerificationCode")
                .contentType(MediaType.APPLICATION_JSON_VALUE).param("verificationType", "email")
                .content(objectMapper.writeValueAsString(verificationRequest))
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));
        then(loginService).should().checkVerificationCode(any(), any());
    }


    @MethodSource
    @ParameterizedTest
    @DisplayName("이메일로 아이디 찾기 Validation 검증 오류 ApiException 에서 테스트 ")
    void findIdByEmail_validationAPiExceptionTest(String name, String email, List<String> field, List<String> message) throws Exception
    {
        //given
        FindUserIdByEmailRequest findUserIdByEmailRequest = FindUserIdByEmailRequest.of(name, email);
        //when
        ResultActions resultActions = mvc.perform(post("/login/findId/email")
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


    private LoginRequest createLoginRequest() {
        return LoginRequest.of("test@naver.com", "abc1234*");
    }

    private TokenResponse createTokenResponse() {
        return TokenResponse.of("accessToken", "refreshToken");
    }



}