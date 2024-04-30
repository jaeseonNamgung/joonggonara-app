package com.hit.joonggonara.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.hit.joonggonara.dto.login.request.SignUpPhoneNumberRequest;
import com.hit.joonggonara.dto.request.login.SignUpRequest;
import com.hit.joonggonara.dto.request.login.VerificationRequest;
import com.hit.joonggonara.service.login.SignUpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = SignApiController.class)
class SignApiControllerTest {

    @MockBean
    private SignUpService signUpService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][POST] 회원가입 테스트")
    void signUpTest() throws Exception {
        //given
        SignUpRequest signUpRequest = createSignUpRequest();
        given(signUpService.signUp(any())).willReturn(true);
        //when & then

        mvc.perform(post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));
    }

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][GET] 중복된 ID가 없을 경우 true를 리턴")
    void NoDuplicateUserIdTest() throws Exception
    {
        //given
        given(signUpService.checkDuplicateUserId(any())).willReturn(true);
        //when & then
        mvc.perform(get("/user/signUp/duplicateUserId")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("userId", "testId")
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));
        then(signUpService).should().checkDuplicateUserId(any());
    }

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][GET] 중복된 ID일 경우 false를 리턴")
    void duplicateUserIdTest() throws Exception
    {
        //given
        given(signUpService.checkDuplicateUserId(any())).willReturn(false);
        //when & then
        mvc.perform(get("/user/signUp/duplicateUserId")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("userId", "testId")
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(false));
        then(signUpService).should().checkDuplicateUserId(any());
    }

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][POST] 인증코드가 정상 적으로 발송이 될 경우 true를 리턴")
    void sendVerificationSuccessfulTest() throws Exception
    {
        //given
        SignUpPhoneNumberRequest phoneNumberRequest =
                SignUpPhoneNumberRequest.of("+8612345678");
        given(signUpService.sendSmsVerificationCode(any())).willReturn(true);
        //when & then
        mvc.perform(post("/user/signUp/sms/verification")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(phoneNumberRequest))
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));
        then(signUpService).should().sendSmsVerificationCode(any());
    }

    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][POST] 일치하는 인증코드 일 경우 true를 리턴")
    void verificationMatchTest() throws Exception
    {
        //given
        VerificationRequest verificationRequest =
                VerificationRequest.of("+8612345678", "123456");
        given(signUpService.checkCode(any())).willReturn(true);
        //when & then
        mvc.perform(post("/user/signUp/sms/checkCode")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(verificationRequest))
                        .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));
        then(signUpService).should().checkCode(any());
    }
    @WithMockUser(roles = "GUEST")
    @Test
    @DisplayName("[API][POST] 일치하는 인증코드 아닐 경우 false를 리턴")
    void verificationNotMatchTest() throws Exception
    {
        //given
        VerificationRequest verificationRequest =
                VerificationRequest.of("+8612345678", "123456");
        given(signUpService.checkCode(any())).willReturn(false);
        //when & then
        mvc.perform(post("/user/signUp/sms/checkCode")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(verificationRequest))
                        .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(false));
        then(signUpService).should().checkCode(any());
    }



    private SignUpRequest createSignUpRequest() {
        return SignUpRequest.of(
                "email@naver.com",
                "Abc123456*",
                "hong",
                "nickName",
                "hit",
                "010-1234-1234"
        );

    }
}