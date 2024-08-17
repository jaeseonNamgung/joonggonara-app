package com.hit.joonggonara.dto.login;

import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.entity.Member;

import java.util.Map;

public record OAuth2Attribute(
        Map<String, Object> attribute,
        String nameAttributeKey,
        String name,
        String email,
        LoginType loginType
) {


    public static OAuth2Attribute of(String provider, Map<String, Object> attributes){

        switch (provider){
            case "google" -> {
                return ofGoogle(attributes);
            }
            case "naver" ->{
                return ofNaver(attributes);
            }
            default -> {
                return ofKakao(attributes);
            }
        }
    }

    private static OAuth2Attribute ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> map = (Map<String, Object>) kakaoAccount.get("profile");

        return new OAuth2Attribute(
                map,
                "id",
                (String) map.get("nickName"),
                (String) map.get("principal"),
                LoginType.KAKAO
        );

    }

    private static OAuth2Attribute ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return new OAuth2Attribute(
                response,
                "response",
                (String) response.get("name"),
                (String) response.get("principal"),
                LoginType.NAVER
        );
    }

    private static OAuth2Attribute ofGoogle(Map<String, Object> attributes) {
        return new OAuth2Attribute(
                attributes,
                "sub",
                (String) attributes.get("name"),
                (String) attributes.get("principal"),
                LoginType.GOGGLE
        );
    }

    public Member toEntity(Role role) {
        return Member.builder()
                .name(name)
                .email(email)
                .role(role)
                .loginType(loginType)
                .build();
    }
}
