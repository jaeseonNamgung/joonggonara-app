package com.hit.joonggonara.common.properties;

import com.hit.joonggonara.common.properties.secretConfig.GoogleSecurityConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GoogleProperties extends OAuth2Properties {

    private final GoogleSecurityConfig googleSecurityConfig;
    private final String requestTokenUri;
    private final String loginUri;
    private final String oidcUri;

    private final String iss;

    public GoogleProperties(GoogleSecurityConfig googleSecurityConfig) {
        this.googleSecurityConfig = googleSecurityConfig;
        this.requestTokenUri = "https://oauth2.googleapis.com/token";
        this.loginUri = "https://accounts.google.com/o/oauth2/v2/auth";
        this.oidcUri = "https://www.googleapis.com/oauth2/v3/certs";
        this.iss = "https://accounts.google.com";
    }


}
