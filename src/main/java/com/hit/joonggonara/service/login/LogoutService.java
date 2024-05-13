package com.hit.joonggonara.service.login;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.properties.RedisProperties;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.common.util.RedisUtil;
import io.jsonwebtoken.lang.Strings;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LogoutService {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;


    @Transactional
    public boolean logout(HttpServletRequest request){
        String accessToken = getParseJwt(request.getHeader(JwtProperties.AUTHORIZATION));
        if(accessToken == null){
            throw new CustomException(UserErrorCode.ALREADY_LOGGED_OUT_USER);
        }
        String userId = jwtUtil.getPrincipal(accessToken);

        redisUtil.delete(RedisProperties.REFRESH_TOKEN_KEY + userId);
        redisUtil.addBlackList(accessToken);
        return true;
    }

    private String getParseJwt(String token) {

        if(Strings.hasText(token) && token.startsWith(JwtProperties.JWT_TYPE)){
            return token.substring(7);
        }

        return null;
    }
}
