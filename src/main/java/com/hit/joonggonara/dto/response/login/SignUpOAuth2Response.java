package com.hit.joonggonara.dto.response.login;

public record SignUpOAuth2Response(
        String name,
        String email

) {
    public static SignUpOAuth2Response of(String name, String email){
        return new SignUpOAuth2Response(name, email);
    }
}
