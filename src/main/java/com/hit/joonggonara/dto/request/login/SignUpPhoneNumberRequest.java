package com.hit.joonggonara.dto.request.login;

public record SignUpPhoneNumberRequest(
        String phoneNumber
) {
    public static SignUpPhoneNumberRequest of(String phoneNumber){
        return new SignUpPhoneNumberRequest(phoneNumber);
    }
}
