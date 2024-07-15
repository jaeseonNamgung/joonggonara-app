package com.hit.joonggonara.dto.response.login;

public record FindUserIdResponse(
        String userId
) {
    public static FindUserIdResponse of(String authenticationValue){
        return new FindUserIdResponse(authenticationValue);
    }
}
