package com.hit.joonggonara.service.login.condition;

public record VerificationCondition(
        String username,

        // 패스워드 찾기 일 경우 userId 필요
        String userId,

        // 인증 방식 : email or verificationKey
        String verificationCode
) {

    // 아이디 찾기
    public static VerificationCondition of(
            String username,
            String verificationCode
    ){
        return new VerificationCondition(username, null, verificationCode);
    }
    // 비밀번호 찾기
    public static VerificationCondition of(
            String username,
            String userId,
            String verificationCode
    ){
        return new VerificationCondition(username, userId, verificationCode);
    }


}
