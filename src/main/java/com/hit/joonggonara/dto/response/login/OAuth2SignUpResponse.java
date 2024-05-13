package com.hit.joonggonara.dto.response.login;

import com.hit.joonggonara.entity.Member;

public record OAuth2SignUpResponse(
        String name,
        String email
) {
    public static OAuth2SignUpResponse of(String name, String email){
        return new OAuth2SignUpResponse(name, email);
    }
    public static OAuth2SignUpResponse of(Member member){
        return OAuth2SignUpResponse.of(member.getName(), member.getEmail());
    }
}
