package com.hit.joonggonara.common.custom.login;


import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.properties.RedisProperties;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.common.util.RedisUtil;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.catchRuntimeException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class CustomJwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RedisUtil redisUtil;
    @InjectMocks
    private CustomJwtFilter sut;


    @Test
    @DisplayName("[Filter] jwt 토큰 필터 성공 테스트")
    void jwtFilterSuccessTest() throws Exception
    {
        //given
        String token = JwtProperties.JWT_TYPE + " " + "token test";
        String userId = "testId";
        Role role = Role.ROLE_USER;
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FilterChain mockFilterChain = mock(FilterChain.class);

        mockRequest.addHeader(JwtProperties.AUTHORIZATION, token);

        given(redisUtil.get(any())).willReturn(Optional.empty());
        given(jwtUtil.validateToken(any())).willReturn(true);
        given(jwtUtil.getPrincipal(any())).willReturn(userId);
        given(jwtUtil.getRole(any())).willReturn(role);
        //when
        sut.doFilter(mockRequest, mockResponse, mockFilterChain);

        //then
        then(redisUtil).should().get(any());
        then(jwtUtil).should().validateToken(any());
        then(jwtUtil).should().getPrincipal(any());
        then(jwtUtil).should().getRole(any());
    }

    @Test
    @DisplayName("[Filter] BlackList에 값이 있다면 ALREADY_LOGGED_OUT_USER 에러 발생")
    void existBlackListTest() throws Exception
    {
        //given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FilterChain mockFilterChain = mock(FilterChain.class);
        given(redisUtil.get(any())).willReturn(Optional.of(RedisProperties.BLACK_LIST_VALUE));
        //when
        CustomException expectedException =
            (CustomException)catchRuntimeException(()->sut.doFilter(mockRequest, mockResponse, mockFilterChain));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.ALREADY_LOGGED_OUT_USER.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.ALREADY_LOGGED_OUT_USER.getMessage());

        then(redisUtil).should().get(any());
    }

}