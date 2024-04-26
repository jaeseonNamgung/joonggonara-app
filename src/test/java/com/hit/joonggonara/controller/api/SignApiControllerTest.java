package com.hit.joonggonara.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.hit.joonggonara.dto.request.login.SignUpRequest;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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