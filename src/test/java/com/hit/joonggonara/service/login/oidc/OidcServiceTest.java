package com.hit.joonggonara.service.login.oidc;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.dto.login.JwkDto;
import com.hit.joonggonara.dto.login.OAuth2PropertiesDto;
import com.hit.joonggonara.dto.login.OidcKidDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OidcServiceTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private KakaoOidcClient kakaoOidcClient;
    @InjectMocks
    private OidcService sut;


    @Test
    @DisplayName("[카카오][OIDC] 유효한 토큰이 주어졌을 때 email을 반환")
    void shouldReturnOidcPayloadWhenTokenIsValid() throws Exception
    {
        //given
        String email = "test@email.com";
        String kid = "test-kid";
        String token = "token";
        String modulus = "mod123";
        String exponent = "exp123";
        OidcKidDto oidcKidDto = new OidcKidDto(List.of(new JwkDto(kid, "RSA", "RS256", "sig", modulus, exponent)));

        OAuth2PropertiesDto oAuth2PropertiesDto = createOAuth2PropertiesDto();
        given(kakaoOidcClient.getKakaoKids(any())).willReturn(oidcKidDto);
        given(jwtUtil.getKid(any(), any(), any())).willReturn(kid);
        given(jwtUtil.getOidcTokenBody(any(), any(), any())).willReturn(email);
        //when
        String expectedValue = sut.getUserInfoFromIdToken(token, oAuth2PropertiesDto);
        //then

        assertThat(expectedValue).isEqualTo(email);

        then(kakaoOidcClient).should().getKakaoKids(any());
        then(jwtUtil).should().getKid(any(), any(), any());
        then(jwtUtil).should().getOidcTokenBody(any(), any(), any());
    }

    @Test
    @DisplayName("[카카오][OIDC] Id token에서 받은 Kid 가 카카오에서 받아온 kid와 일치하니 않을 경우 KID_REQUEST_FAILED 에러 발생")
    void shouldThrowKID_REQUEST_FAILEDErrorWhenNotMatchKid() throws Exception
    {
        //given
        String kid = "test-kid";
        String token = "token";
        String modulus = "mod123";
        String exponent = "exp123";
        OidcKidDto oidcKidDto = new OidcKidDto(List.of(new JwkDto("not match test-kid", "RSA", "RS256", "sig", modulus, exponent)));
        OAuth2PropertiesDto oAuth2PropertiesDto = createOAuth2PropertiesDto();
        given(kakaoOidcClient.getKakaoKids(any())).willReturn(oidcKidDto);
        given(jwtUtil.getKid(any(), any(), any())).willReturn(kid);
        //when
        CustomException expectedException =
            (CustomException) catchException(()->sut.getUserInfoFromIdToken(token, oAuth2PropertiesDto));
        //then
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(UserErrorCode.KID_REQUEST_FAILED.getHttpStatus());
        assertThat(expectedException).hasMessage(UserErrorCode.KID_REQUEST_FAILED.getMessage());
        then(kakaoOidcClient).should().getKakaoKids(any());
        then(jwtUtil).should().getKid(any(), any(), any());
    }


    private OAuth2PropertiesDto createOAuth2PropertiesDto() {
        return OAuth2PropertiesDto.of(
                "Authorization",
                "Bearer access_token",
                "grant_type",
                "authorization_code",
                "client_id",
                "client_secret",
                "redirect_uri",
                "12345",
                "RS256",
                "code",
                "https://example.com/oauth2/token",
                "https://example.com/.well-known/openid-configuration",
                "https://example.com",
                "https://example.com/login",
                "your-client-id",
                "https://yourapp.com/callback",
                "your-client-secret",
                "openid profile email",
                "state",
                "RANDOM_STRING",
                "https://example.com/user-info"
        );
    }

}