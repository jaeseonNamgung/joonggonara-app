package com.hit.joonggonara.dto.login.request;

public record SignUpPhoneNumberRequest(
        String phoneNumber
) {
    public static SignUpPhoneNumberRequest of(String phoneNumber){
        return new SignUpPhoneNumberRequest(phoneNumber);
    }
}
