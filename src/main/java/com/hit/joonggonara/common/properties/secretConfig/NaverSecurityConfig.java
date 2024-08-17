package com.hit.joonggonara.common.properties.secretConfig;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class NaverSecurityConfig {


    private final String clientId;

    private final String redirectUri;

    private final String clientSecret;


    public NaverSecurityConfig(
            @Value("${naver.client-id}")
            String clientId,
            @Value("${naver.redirect-uri}")
            String redirectUri,
            @Value("${naver.client-secret}")
            String clientSecret) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
    }

}
