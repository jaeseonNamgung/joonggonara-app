package com.hit.joonggonara.service.login.oidc;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.dto.login.JwkDto;
import com.hit.joonggonara.dto.login.OAuth2PropertiesDto;
import com.hit.joonggonara.dto.login.OidcKidDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;




@RequiredArgsConstructor
@Service
public class OidcService {

    private final JwtUtil jwtUtil;
    private final KakaoOidcClient oidcClient;

    public String getUserInfoFromIdToken(String token, OAuth2PropertiesDto oAuth2PropertiesDto){
        // 소셜 로그인에서 제공하는 공개 키를 가져오고 캐싱
        OidcKidDto oidcKidDto = oidcClient.getKakaoKids(oAuth2PropertiesDto.oidcUri());
        // 발급 받은 Id token에 kid를 추출
        String kid = getKidFromUnsignedIdToken(token, oAuth2PropertiesDto.iss(), oAuth2PropertiesDto.clientIdValue());
        // 소셜 로그인에서 가져온 KID와 Id Token에서 가져온 KID와 일치하는 공개키를 찾는다.
        JwkDto jwkDto = findOidcKid(kid, oidcKidDto);
        // modulus와 exponent로 RSA로 암호화된 public 키를 생성 후 생성한 키로 Id Token에서 Claim을 가져오고 Dto로 변환
        return jwtUtil.getOidcTokenBody(token, jwkDto.n(), jwkDto.e());
    }
    private JwkDto findOidcKid(String kid, OidcKidDto oidcKidDto) {
        return oidcKidDto.keys().stream()
                .filter(key-> key.kid().equals(kid))
                .findFirst()
                .orElseThrow(()-> new CustomException(UserErrorCode.KID_REQUEST_FAILED));
    }
    private String getKidFromUnsignedIdToken(String token, String issuer, String audience) {
        return jwtUtil.getKid(token, issuer, audience);
    }
}
