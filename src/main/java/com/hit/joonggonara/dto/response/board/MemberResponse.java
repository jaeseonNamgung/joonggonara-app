package com.hit.joonggonara.dto.response.board;

import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.entity.Member;

public record MemberResponse(
        Long id,
        String userId,
        String email,
        String name,
        String nickName,
        String profile,
        LoginType loginType
) {

    public static MemberResponse of(
            Long id,
            String userId,
            String email,
            String name,
            String nickName,
            String profile,
            LoginType loginType
    ) {
        return new MemberResponse(id, userId, email, name, nickName, profile, loginType);
    }

    public static MemberResponse fromResponse(Member member) {
        return MemberResponse.of(
                member.getId(),
                member.getUserId(),
                member.getEmail(),
                member.getName(),
                member.getNickName(),
                member.getProfile(),
                member.getLoginType()
        );
    }
}
