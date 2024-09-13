package com.hit.joonggonara.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.hit.joonggonara.common.properties.JwtProperties.REFRESH_TOKEN_NAME;

@Component
public class CookieUtil {

    private final static int MAX_AG = 60 * 60 * 24;

    public void addCookie(HttpServletResponse response, String key, String value){
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");

        //  Client에서 Javascript를 통한 쿠키 탈취 문제를 예방
        // XSS 공격 차단
        cookie.setHttpOnly(true);
        // Cookie 저장 기간 설정
        cookie.setMaxAge(MAX_AG);
        response.addCookie(cookie);
    }

    public Optional<Cookie> getCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(REFRESH_TOKEN_NAME)){
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public void deleteCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(REFRESH_TOKEN_NAME)){
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }
}
