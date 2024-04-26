package com.hit.joonggonara.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private final static int MAX_AG = 60 * 60 * 24;

    public static void addCookie(HttpServletResponse response, String key, String value){
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");

        //  Client에서 Javascript를 통한 쿠키 탈취 문제를 예방
        // XSS 공격 차단
        cookie.setHttpOnly(true);
        // Cookie 저장 기간 설정
        cookie.setMaxAge(MAX_AG);
        response.addCookie(cookie);
    }
}
