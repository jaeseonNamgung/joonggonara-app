package com.hit.joonggonara.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.dto.request.login.MemberUpdateRequest;
import com.hit.joonggonara.dto.request.login.SignUpPhoneNumberRequest;
import com.hit.joonggonara.dto.request.login.VerificationRequest;
import com.hit.joonggonara.service.login.LoginService;
import com.hit.joonggonara.service.login.SignUpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MyPageApiController.class)
class MyPageApiControllerTest {

    
    @MockBean
    private SignUpService signUpService;
    @MockBean
    private LoginService loginService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @WithMockUser(username = "USER")
    @Test
    @DisplayName("[API][Post][Update] 회원 정보 수정")
    void UpdateMemberInfoTest() throws Exception
    {
        //given
        String token = JwtProperties.JWT_TYPE + "token";
        MemberUpdateRequest memberUpdateRequest = createMemberUpdateRequest();
        //when
        mvc.perform(post("/user/update/info")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(memberUpdateRequest))
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));
        //then
        then(loginService).should().memberUpdateInfo(any(), any());
    }

    @WithMockUser(username = "USER")
    @Test
    @DisplayName("[API][Get][Check] 닉네임 중복 검사 - 이미 존재하는 닉네임 true를 리턴")
    void returnTrueIfAlreadyExistNickName() throws Exception
    {
        //given
        String nickName = "nickNameTest";
        given(signUpService.checkNickName(any())).willReturn(true);
        //when
        mvc.perform(get("/user/update/info/nickName/" + nickName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));
        //then
        then(signUpService).should().checkNickName(any());
    }
    @WithMockUser(username = "USER")
    @Test
    @DisplayName("[API][Get][Check] 닉네임 중복 검사 - 존재하지 않은 닉네임 false를 리턴")
    void returnFalseIfNotExistNickName() throws Exception
    {
        //given
        String nickName = "nickNameTest";
        given(signUpService.checkNickName(any())).willReturn(false);
        //when
        mvc.perform(get("/user/update/info/nickName/" + nickName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(false));
        //then
        then(signUpService).should().checkNickName(any());
    }

    @PostMapping("/user/update/info/sms/verification")
    public ResponseEntity<Boolean> sendSms(@RequestBody SignUpPhoneNumberRequest phoneNumberRequest){
        return ResponseEntity.ok(signUpService.sendSmsVerificationCode(phoneNumberRequest));
    }

    @WithMockUser(username = "USER")
    @Test
    @DisplayName("[API][Post][Sms] sms 전송이 성공적으로 전송 될 경우 true를 반환")
    void returnTrueIfSuccessFulSendSms() throws Exception
    {
        //given
        SignUpPhoneNumberRequest signUpPhoneNumberRequest =
                createSignUpPhoneNumberRequest();
        given(signUpService.sendSmsVerificationCode(any())).willReturn(true);
        //when
        mvc.perform(post("/user/update/info/sms/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpPhoneNumberRequest))
                        .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));
        //then
        then(signUpService).should().sendSmsVerificationCode(any());
    }
    @WithMockUser(username = "USER")
    @Test
    @DisplayName("[API][Post][Code] 인증코드가 일치할 경우 true를 리턴")
    void returnTrueIfTheCodeMatches() throws Exception
    {
        //given
        VerificationRequest verificationRequest = 
                createVerificationRequest();
        given(signUpService.checkCode(any())).willReturn(true);
        //when
        mvc.perform(post("/user/update/info/sms/checkCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verificationRequest))
                        .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));
        //then
        then(signUpService).should().checkCode(any());
    }

    @WithMockUser(username = "USER")
    @Test
    @DisplayName("[API][Post][Code] 인증코드가 불일치할 경우 false를 리턴")
    void returnFalseIfTheCodeIsInconsistent() throws Exception
    {
        //given
        VerificationRequest verificationRequest =
                createVerificationRequest();
        given(signUpService.checkCode(any())).willReturn(false);
        //when
        mvc.perform(post("/user/update/info/sms/checkCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verificationRequest))
                        .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(false));
        //then
        then(signUpService).should().checkCode(any());
    }

    private VerificationRequest createVerificationRequest() {
        return VerificationRequest.of("+861754562261", "123456");
    }

    private SignUpPhoneNumberRequest createSignUpPhoneNumberRequest() {
        return SignUpPhoneNumberRequest.of("+861754562261");
    }


    private MemberUpdateRequest createMemberUpdateRequest() {
        return MemberUpdateRequest.of(
                "nickName",
                "test@email.com",
                "+8617545562261",
                "profile",
                true
        );
    }

}