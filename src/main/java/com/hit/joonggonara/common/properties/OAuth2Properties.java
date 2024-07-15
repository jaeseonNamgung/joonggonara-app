package com.hit.joonggonara.common.properties;

import lombok.Getter;
import org.springframework.stereotype.Component;


@Component
@Getter
public abstract  class OAuth2Properties {
    public final String headerName;
    public final String headerValue;
    public final String grantTypeName;
    public final String grantTypeValue;
    public final String clientIdName;
    public final String clientSecretName;
    public final String redirectUriName;
    public final String kid;
    public final String rsa;
    public final String codeName;

    public OAuth2Properties() {
        this.headerName = "Content-type";
        this.headerValue = "application/x-www-form-urlencoded;charset=utf-8";
        this.grantTypeName = "grant_type";
        this.grantTypeValue =  "authorization_code";
        this.clientIdName = "client_id";
        this.clientSecretName = "client_secret";
        this.redirectUriName = "redirect_uri";
        this.kid = "kid";
        this.rsa = "RSA";
        this.codeName = "code";
    }
}
