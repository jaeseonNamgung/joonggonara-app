package com.hit.joonggonara.dto.response.login;

public record OAUth2UserResponse(
        String email,
        String profile,
        Boolean signUpStatus
) {

    public static OAUth2UserResponse of(String email, String profile, Boolean signUpStatus){
        return new OAUth2UserResponse(email,profile, signUpStatus);
    }

    public static OAUth2UserResponse fromResponse(String email,String profile, Boolean signUpStatus){
        return OAUth2UserResponse.of(email,profile, signUpStatus);
    }
}
