package com.hit.joonggonara.common.properties;

import com.hit.joonggonara.common.properties.secretConfig.KakaoSecurityConfig;
import com.hit.joonggonara.common.properties.secretConfig.NaverSecurityConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
public class NaverProperties extends OAuth2Properties{

    private final NaverSecurityConfig naverSecurityConfig;
    private final String requestTokenUri;
    private final String loginUri;
    private final String userInfoUri;
    private final String stateName;
    private final String stateValue;

    public NaverProperties(NaverSecurityConfig naverSecurityConfig) {
        this.naverSecurityConfig = naverSecurityConfig;
        this.requestTokenUri = "https://nid.naver.com/oauth2.0/token";
        this.loginUri = "https://nid.naver.com/oauth2.0/authorize";
        this.userInfoUri = "https://openapi.naver.com/v1/nid/me";
        this.stateName = "state";
        this.stateValue = "RANDOM_STRING";
    }


}
