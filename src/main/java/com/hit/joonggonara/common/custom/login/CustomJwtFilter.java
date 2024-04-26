package com.hit.joonggonara.common.custom.login;

import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.common.util.JwtUtil;
import io.jsonwebtoken.lang.Strings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class CustomJwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = getParseJwt(request);

        // token 이 null 이라면 로그인 하지 않은 User
        // token이 null 이 아니고 유효성 검증이 true라면 로그인 한 회원
        if(Strings.hasText(token) && jwtUtil.validateToken(token)){
            String email = jwtUtil.getEmail(token);
            Role role =  jwtUtil.getRole(token);

            Authentication authentication = new UsernamePasswordAuthenticationToken(email, "",
                    Collections.singleton(new SimpleGrantedAuthority(role.name())));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);

    }

    private String getParseJwt(HttpServletRequest request) {
        String token = request.getHeader(JwtProperties.AUTHORIZATION);

        if(Strings.hasText(token) && token.startsWith(JwtProperties.JWT_TYPE + " ")){
            return token.substring(7);
        }
        return null;
    }
}
