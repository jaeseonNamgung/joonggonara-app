package com.hit.joonggonara.service.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.dto.login.OAuth2TokenDto;
import com.hit.joonggonara.dto.login.OAuth2PropertiesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@Service
public class OAuth2Service {


    public OAuth2TokenDto requestAccessToken(String code, OAuth2PropertiesDto oAuth2PropertiesDto) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(oAuth2PropertiesDto.headerName(), oAuth2PropertiesDto.headerValue());
        HttpEntity<MultiValueMap<String, String>> httpEntity = getRequestAccessTokenBody(code, oAuth2PropertiesDto, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(oAuth2PropertiesDto.requestTokenUri(), httpEntity, String.class);
        Gson gson = new Gson();
        return gson.fromJson(responseEntity.getBody(), OAuth2TokenDto.class);
    }

    private static HttpEntity<MultiValueMap<String, String>> getRequestAccessTokenBody(String code, OAuth2PropertiesDto oAuth2PropertiesDto, HttpHeaders headers) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(oAuth2PropertiesDto.grantTypeName(), oAuth2PropertiesDto.grantTypeValue());
        body.add(oAuth2PropertiesDto.clientIdName(), oAuth2PropertiesDto.clientIdValue());
        body.add(oAuth2PropertiesDto.redirectUriName(), oAuth2PropertiesDto.redirectUriValue());
        body.add(oAuth2PropertiesDto.clientSecretName(), oAuth2PropertiesDto.clientSecretValue());
        body.add(oAuth2PropertiesDto.codeName(), code);

        // Naver 에서는 state 쿼리가 필요
        if(oAuth2PropertiesDto.stateName() != null && oAuth2PropertiesDto.stateValue() != null){
            body.add(oAuth2PropertiesDto.stateName(), oAuth2PropertiesDto.stateValue());
        }

        return new HttpEntity<>(body, headers);
    }

    public String sendRedirect(LoginType loginType, OAuth2PropertiesDto oAuth2PropertiesDto){
        String uri = oAuth2PropertiesDto.loginUri() + "?"
                + oAuth2PropertiesDto.clientIdName() + "=" + oAuth2PropertiesDto.clientIdValue() +
                "&" + oAuth2PropertiesDto.redirectUriName() + "=" + oAuth2PropertiesDto.redirectUriValue()
                + "&response_type=code";

        if(loginType.equals(LoginType.GOGGLE)){
            uri = uri + "&scope=" + oAuth2PropertiesDto.scope();
        } else if (loginType.equals(LoginType.NAVER)) {
            uri = uri + "&" + oAuth2PropertiesDto.stateName() + "=" + oAuth2PropertiesDto.stateValue();
        }
        return uri;
    }


    public String getUserInfoFromAccessToken(String accessToken, OAuth2PropertiesDto oAuth2PropertiesDto) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(oAuth2PropertiesDto.headerName(), oAuth2PropertiesDto.headerValue());
        headers.add(JwtProperties.AUTHORIZATION, JwtProperties.JWT_TYPE + accessToken);

        HttpEntity<HttpHeaders> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<HashMap> responseEntity = restTemplate.postForEntity(oAuth2PropertiesDto.userInfoUrl(), httpEntity, HashMap.class);
        Map<String, String> map = (Map<String, String>) responseEntity.getBody().get("response");
        return map.get("email");
    }
}
