package com.hit.joonggonara.common.properties.secretConfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GoogleSecurityConfig {


    private final String clientId;

    private final String redirectUri;

    private final String clientSecret;

    private final String scope;

    public GoogleSecurityConfig(
            @Value("${google.client-id}")
            String clientId,
            @Value("${google.redirect-uri}")
            String redirectUri,
            @Value("${google.client-secret}")
            String clientSecret,
            @Value("${google.scope}")
            String scope) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
        this.scope = scope;
    }

}
