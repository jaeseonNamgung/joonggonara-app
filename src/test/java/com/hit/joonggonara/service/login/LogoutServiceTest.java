package com.hit.joonggonara.service.login;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.common.util.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RedisUtil redisUtil;
    @InjectMocks
    private LogoutService sut;

    @Test
    @DisplayName("[Service] 성공 적으로 로그아웃 할 경우 true를 리턴")
    void logoutTest() throws Exception
    {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtProperties.AUTHORIZATION, JwtProperties.JWT_TYPE + "token");
        given(jwtUtil.getUserId(any())).willReturn("testId");
        //when
        boolean expectedValue = sut.logout(request);
        //then
        assertThat(expectedValue).isTrue();

        then(redisUtil).should().delete(any());
        then(redisUtil).should().addBlackList(any());
    }

    @Test
    @DisplayName("[Service] token이 null이라면 ALREADY_LOGGED_OUT_USER 에러 발생")
    void logoutTokenNullTest() throws Exception
    {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        //when
        CustomException expectedException =  (CustomException) catchException(()->sut.logout(request));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.ALREADY_LOGGED_OUT_USER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.ALREADY_LOGGED_OUT_USER.getMessage());
    }

}