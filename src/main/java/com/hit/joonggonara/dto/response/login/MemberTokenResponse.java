package com.hit.joonggonara.dto.response.login;


import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.dto.login.TokenDto;
import com.hit.joonggonara.dto.response.board.MemberResponse;
import com.hit.joonggonara.entity.Member;

public record MemberTokenResponse(
        MemberResponse memberResponse,
        String accessToken,
        String refreshToken
) {
    public static MemberTokenResponse of(MemberResponse memberResponse, String accessToken, String refreshToken){
        return new MemberTokenResponse(memberResponse, accessToken, refreshToken);
    }

    public static MemberTokenResponse toResponse(Member member, TokenDto tokenDto){
        return MemberTokenResponse.of(
                MemberResponse.fromResponse(member),
                tokenDto.accessToken(),
                tokenDto.refreshToken());
    }

    public static MemberTokenResponse toResponse(String email, String profile, LoginType loginType){
        return MemberTokenResponse.of(
                MemberResponse.fromResponse(email, profile, loginType),
                null,
                null);
    }
}
