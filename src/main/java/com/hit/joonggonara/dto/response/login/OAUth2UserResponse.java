package com.hit.joonggonara.dto.response.login;

public record OAUth2UserResponse(
        String email,
        Boolean signUpStatus
) {

    public static OAUth2UserResponse of(String email, Boolean signUpStatus){
        return new OAUth2UserResponse(email, signUpStatus);
    }

    public static OAUth2UserResponse fromResponse(String email, Boolean signUpStatus){
        return OAUth2UserResponse.of(email, signUpStatus);
    }
}
