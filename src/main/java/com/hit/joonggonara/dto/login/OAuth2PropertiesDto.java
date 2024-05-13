package com.hit.joonggonara.dto.login;

import com.hit.joonggonara.common.properties.GoogleProperties;
import com.hit.joonggonara.common.properties.KakaoProperties;
import com.hit.joonggonara.common.properties.NaverProperties;

public record OAuth2PropertiesDto(
        String headerName,
        String headerValue,
        String grantTypeName,
        String grantTypeValue,
        String clientIdName,
        String clientSecretName,
        String redirectUriName,
        String kid,
        String rsa,
        String codeName,
        String requestTokenUri,
        String oidcUri,
        String iss,
        String loginUri,
        String clientIdValue,
        String redirectUriValue,
        String clientSecretValue,
        String scope,
        String stateName,
        String stateValue,
        String userInfoUrl
) {

    public static OAuth2PropertiesDto of(
            String headerName,
            String headerValue,
            String grantTypeName,
            String grantTypeValue,
            String clientIdName,
            String clientSecretName,
            String redirectUriName,
            String kid,
            String rsa,
            String codeName,
            String requestTokenUri,
            String oidcUri,
            String iss,
            String loginUri,
            String clientId,
            String redirectUri,
            String clientSecret,
            String scope,
            String stateName,
            String stateValue,
            String userInfoUrl

    ){
        return new OAuth2PropertiesDto(
                headerName,
                headerValue,
                grantTypeName,
                grantTypeValue,
                clientIdName,
                clientSecretName,
                redirectUriName,
                kid,
                rsa,
                codeName,
                requestTokenUri,
                oidcUri,
                iss,
                loginUri,
                clientId,
                redirectUri,
                clientSecret,
                scope,
                stateName,
                stateValue,
                userInfoUrl
        );
    }
    public static OAuth2PropertiesDto fromDto(KakaoProperties kakaoProperties){
        return OAuth2PropertiesDto.of(
                kakaoProperties.getHeaderName(),
                kakaoProperties.getHeaderValue(),
                kakaoProperties.getGrantTypeName(),
                kakaoProperties.getGrantTypeValue(),
                kakaoProperties.getClientIdName(),
                kakaoProperties.getClientSecretName(),
                kakaoProperties.getRedirectUriName(),
                kakaoProperties.getKid(),
                kakaoProperties.getRsa(),
                kakaoProperties.getCodeName(),
                kakaoProperties.getRequestTokenUri(),
                kakaoProperties.getOidcUri(),
                kakaoProperties.getIss(),
                kakaoProperties.getLoginUri(),
                kakaoProperties.getKakaoSecurityConfig().getClientId(),
                kakaoProperties.getKakaoSecurityConfig().getRedirectUri(),
                kakaoProperties.getKakaoSecurityConfig().getClientSecret(),
                null,
                null,
                null,
                null
                );
    }

    public static OAuth2PropertiesDto fromDto(GoogleProperties googleProperties){
        return OAuth2PropertiesDto.of(
                googleProperties.getHeaderName(),
                googleProperties.getHeaderValue(),
                googleProperties.getGrantTypeName(),
                googleProperties.getGrantTypeValue(),
                googleProperties.getClientIdName(),
                googleProperties.getClientSecretName(),
                googleProperties.getRedirectUriName(),
                googleProperties.getKid(),
                googleProperties.getRsa(),
                googleProperties.getCodeName(),
                googleProperties.getRequestTokenUri(),
                googleProperties.getOidcUri(),
                googleProperties.getIss(),
                googleProperties.getLoginUri(),
                googleProperties.getGoogleSecurityConfig().getClientId(),
                googleProperties.getGoogleSecurityConfig().getRedirectUri(),
                googleProperties.getGoogleSecurityConfig().getClientSecret(),
                googleProperties.getGoogleSecurityConfig().getScope(),
                null,
                null,
                null
        );

    }

    public static OAuth2PropertiesDto fromDto(NaverProperties naverProperties) {
        return OAuth2PropertiesDto.of(
                naverProperties.getHeaderName(),
                naverProperties.getHeaderValue(),
                naverProperties.getGrantTypeName(),
                naverProperties.getGrantTypeValue(),
                naverProperties.getClientIdName(),
                naverProperties.getClientSecretName(),
                naverProperties.getRedirectUriName(),
                naverProperties.getKid(),
                naverProperties.getRsa(),
                naverProperties.getCodeName(),
                naverProperties.getRequestTokenUri(),
                null,
                null,
                naverProperties.getLoginUri(),
                naverProperties.getNaverSecurityConfig().getClientId(),
                naverProperties.getNaverSecurityConfig().getRedirectUri(),
                naverProperties.getNaverSecurityConfig().getClientSecret(),
                null,
                naverProperties.getStateName(),
                naverProperties.getStateValue(),
                naverProperties.getUserInfoUri()
        );
    }
}
