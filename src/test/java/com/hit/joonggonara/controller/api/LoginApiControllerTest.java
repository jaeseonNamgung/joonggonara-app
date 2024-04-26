package com.hit.joonggonara.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.joonggonara.config.SecurityConfig;
import com.hit.joonggonara.dto.request.LoginRequest;
import com.hit.joonggonara.dto.request.PhoneNumberRequest;
import com.hit.joonggonara.dto.request.SmsVerificationRequest;
import com.hit.joonggonara.dto.response.TokenResponse;
import com.hit.joonggonara.error.CustomException;
import com.hit.joonggonara.error.ErrorCode;
import com.hit.joonggonara.error.errorCode.UserErrorCode;
import com.hit.joonggonara.properties.JwtProperties;
import com.hit.joonggonara.service.user.LoginService;
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

import static org.assertj.core.api.Assertions.assertThat;
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
                Arguments.of("email", "abc1234*", List.of("email"), List.of("이메일 주소를 정확히 입력해주세요.")),
                Arguments.of(" ", "abc1234*", List.of("email"), List.of("이메일 또는 비밀번호를 입력해주세요.")),
                Arguments.of("email@naver.com", " ", List.of("password"), List.of("이메일 또는 비밀번호를 입력해주세요.")),
                Arguments.of(null, "abc1234*", List.of("email"), List.of("이메일 또는 비밀번호를 입력해주세요.")),
                Arguments.of("email@naver.com", null, List.of("password"), List.of("이메일 또는 비밀번호를 입력해주세요.")),
                Arguments.of("", "", List.of("email", "password"), List.of("이메일 또는 비밀번호를 입력해주세요.", "이메일 또는 비밀번호를 입력해주세요.")),
                Arguments.of(null, null,List.of("email", "password"), List.of("이메일 또는 비밀번호를 입력해주세요.", "이메일 또는 비밀번호를 입력해주세요."))
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
    @DisplayName("[API][POST] 핸드폰 인증 검사 성공")
    void successfulPhoneNumberVerificationTest() throws Exception
    {
        //given
        PhoneNumberRequest phoneNumberRequest = createPhoneNumberRequest();
        given(loginService.checkPhoneNumber(any())).willReturn(true);
        //when & then
        mvc.perform(post("/login/checkPhoneNumber")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(phoneNumberRequest))
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));
        then(loginService).should().checkPhoneNumber(any());
    }

    @Test
    @DisplayName("[API][POST] 인증 코드 검사")
    void checkVerificationCodeTest() throws Exception
    {
        //given
        SmsVerificationRequest smsVerificationRequest =
                createSmsVerificationRequest();
        given(loginService.checkSmsVerificationCode(any())).willReturn(true);
        //when & then
        mvc.perform(post("/login/checkVerificationCode")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(smsVerificationRequest))
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));

        then(loginService).should().checkSmsVerificationCode(any());
    }

    private SmsVerificationRequest createSmsVerificationRequest() {
        return SmsVerificationRequest.of("+8612345678", "123456");
    }

    private PhoneNumberRequest createPhoneNumberRequest() {
        return PhoneNumberRequest.of("+8612345678");
    }

    private LoginRequest createLoginRequest() {
        return LoginRequest.of("test@naver.com", "abc1234*");
    }

    private TokenResponse createTokenResponse() {
        return TokenResponse.of("accessToken", "refreshToken");
    }

    private String createToken() {
        return JwtProperties.JWT_TYPE + " " + "token test";
    }


}