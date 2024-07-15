package com.hit.joonggonara.common.properties.secretConfig;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KakaoSecurityConfig {
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String scope;

    public KakaoSecurityConfig(
            @Value("${kakao.client-id}")
            String clientId,
            @Value("${kakao.client-secret}")
            String clientSecret,
            @Value("${kakao.redirect-uri}")
            String redirectUri,
            @Value("${kakao.scope}")
            String scope

    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.scope = scope;
    }
}
