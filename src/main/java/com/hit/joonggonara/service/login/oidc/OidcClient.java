package com.hit.joonggonara.service.login.oidc;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.dto.login.OidcKidDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OidcClient {


    // 공개 키를 요청하는 메서드
    // 재사용 목적으로 캐시 사용
    @Cacheable(cacheNames = "kakao_oidc_kid", cacheManager = "redisCacheManager")
    public OidcKidDto getKakaoKids(String oidcUri){

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<OidcKidDto> oidcKidEntity =
                restTemplate.getForEntity(oidcUri, OidcKidDto.class);

        if(oidcKidEntity.getStatusCode() == HttpStatus.OK){
            return oidcKidEntity.getBody();
        }else{
            throw new CustomException(UserErrorCode.KID_REQUEST_FAILED);
        }
    }
    @Cacheable(cacheNames = "google_oidc_kid", cacheManager = "redisCacheManager")
    public OidcKidDto getGoogleKids(String oidcUri){

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<OidcKidDto> oidcKidEntity =
                restTemplate.getForEntity(oidcUri, OidcKidDto.class);

        if(oidcKidEntity.getStatusCode() == HttpStatus.OK){
            return oidcKidEntity.getBody();
        }else{
            throw new CustomException(UserErrorCode.KID_REQUEST_FAILED);
        }
    }
}
