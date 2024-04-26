package com.hit.joonggonara.custom.login;


import com.hit.joonggonara.common.custom.login.CustomJwtFilter;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.common.type.Role;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class CustomJwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private CustomJwtFilter sut;


    @Test
    @DisplayName("[Filter] jwt 토큰 필터 성공 테스트 ")
    void jwtFilterSuccessTest() throws Exception
    {
        //given
        String token = JwtProperties.JWT_TYPE + " " + "token test";
        String email = "test@naver.com";
        Role role = Role.USER;
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FilterChain mockFilterChain = mock(FilterChain.class);

        mockRequest.addHeader(JwtProperties.AUTHORIZATION, token);

        given(jwtUtil.validateToken(any())).willReturn(true);
        given(jwtUtil.getEmail(any())).willReturn(email);
        given(jwtUtil.getRole(any())).willReturn(role);
        //when
        sut.doFilter(mockRequest, mockResponse, mockFilterChain);

        //then
        then(jwtUtil).should().validateToken(any());
        then(jwtUtil).should().getEmail(any());
        then(jwtUtil).should().getRole(any());
    }

}