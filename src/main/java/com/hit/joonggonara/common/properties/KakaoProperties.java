package com.hit.joonggonara.common.properties;

import com.hit.joonggonara.common.properties.secretConfig.KakaoSecurityConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
public class KakaoProperties extends OAuth2Properties{

    private final KakaoSecurityConfig kakaoSecurityConfig;
    private final String requestTokenUri;
    private final String oidcUri;

    private final String iss;
    private final String loginUri;


    public KakaoProperties(KakaoSecurityConfig kakaoSecurityConfig) {
        this.kakaoSecurityConfig = kakaoSecurityConfig;
        this.requestTokenUri = "https://kauth.kakao.com/oauth/token";
        this.oidcUri = "https://kauth.kakao.com/.well-known/jwks.json";
        this.iss = "https://kauth.kakao.com";
        this.loginUri = "https://kauth.kakao.com/oauth/authorize";
    }


}
