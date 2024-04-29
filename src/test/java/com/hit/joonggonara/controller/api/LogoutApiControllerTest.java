package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.common.util.CookieUtil;
import com.hit.joonggonara.service.login.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers= LogoutApiController.class)
class LogoutApiControllerTest {

    @MockBean
    private LogoutService logoutService;

    @MockBean
    private CookieUtil cookieUtil;

    @Autowired
    private MockMvc mvc;

    @WithMockUser(roles = "USER")
    @Test
    @DisplayName("[API][POST] 로그아웃 성공 시 true를 반환")
    void logoutTest() throws Exception
    {
        //given
        given(logoutService.logout(any())).willReturn(true);
        //when
        mvc.perform(delete("/user/logout")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").value(true));

        //then
        then(cookieUtil).should().deleteCookie(any(), any());
        then(logoutService).should().logout(any());
    }


}