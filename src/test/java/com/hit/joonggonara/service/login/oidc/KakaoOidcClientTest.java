package com.hit.joonggonara.service.login.oidc;

import com.hit.joonggonara.common.properties.KakaoProperties;
import com.hit.joonggonara.common.properties.secretConfig.KakaoSecurityConfig;
import com.hit.joonggonara.dto.login.JwkDto;
import com.hit.joonggonara.dto.login.OidcKidDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


@SpringBootTest
class KakaoOidcClientTest {
    
    @Autowired
    private KakaoOidcClient sut;

    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    private KakaoSecurityConfig kakaoSecurityConfig;

    @Test
    @DisplayName("[카카오][OIDC] 카카오 OIDC 공개 키 요청 테스트")
    void requestKakaoOIDCPublicKeysTest() throws Exception
    {
        KakaoProperties kakaoProperties = new KakaoProperties(kakaoSecurityConfig);
        OidcKidDto oidcKidDto =
                new OidcKidDto(List.of(new JwkDto("kid", "kty", "alg", "use", "n", "e")));
        given(restTemplate.getForEntity(any(), eq(OidcKidDto.class))).willReturn(ResponseEntity.ok(oidcKidDto));

        OidcKidDto kakaoKid = sut.getKakaoKids(kakaoProperties.getOidcUri());
        assertThat(kakaoKid).isNotNull();
        assertThat(kakaoKid.keys()).extracting("kty").contains("RSA");
        assertThat(kakaoKid.keys()).extracting("alg").contains("RS256");
        assertThat(kakaoKid.keys()).extracting("use").contains("sig");
        assertThat(kakaoKid.keys()).extracting("e").contains("AQAB");

        then(restTemplate).should(atMost(1)).getForEntity(any(), eq(OidcKidDto.class));
    }
    @Test
    @DisplayName("[카카오][Cache] 캐시 적용 후 메서드 호출이 1번 호출되는지 테스트")
    void  checkOIDCPublicKeyIsStoredInRedisAfterRequestOIDCPublicKeyTest() throws Exception
    {
        KakaoProperties kakaoProperties = new KakaoProperties(kakaoSecurityConfig);
        OidcKidDto oidcKidDto =
                new OidcKidDto(List.of(new JwkDto("kid", "kty", "alg", "use", "n", "e")));

        given(restTemplate.getForEntity(any(), eq(OidcKidDto.class))).willReturn(ResponseEntity.ok(oidcKidDto));

        OidcKidDto expectedOidcKidDto = sut.getKakaoKids(kakaoProperties.getOidcUri());
        OidcKidDto expectedOidcKidDto2 = sut.getKakaoKids(kakaoProperties.getOidcUri());
        assertThat(expectedOidcKidDto).isNotNull();
        assertThat(expectedOidcKidDto2).isNotNull();
        assertThat(expectedOidcKidDto2).isEqualTo(expectedOidcKidDto);

        then(restTemplate).should(atMost(1)).getForEntity(any(), eq(OidcKidDto.class));
    }

}