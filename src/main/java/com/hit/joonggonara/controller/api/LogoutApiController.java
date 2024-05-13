package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.common.util.CookieUtil;
import com.hit.joonggonara.service.login.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LogoutApiController {

    private final LogoutService logoutService;
    private final CookieUtil cookieUtil;

    @DeleteMapping("/user/logout")
    public ResponseEntity<Boolean> logout(HttpServletRequest request, HttpServletResponse response){
        boolean isLogout = logoutService.logout(request);
        if(isLogout){
            cookieUtil.deleteCookie(request, response);
        }
        return ResponseEntity.ok(isLogout);
    }

}
