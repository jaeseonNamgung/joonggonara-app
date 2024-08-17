package com.hit.joonggonara.common.type;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.BaseErrorCode;

public enum VerificationType {
    ID_EMAIL, ID_SMS, PASSWORD_EMAIL, PASSWORD_SMS, EMAIL, SMS;


    public static VerificationType toEnum(String verificationType){
        verificationType  = verificationType.toUpperCase();

        switch (verificationType){
            case "EMAIL" -> {return EMAIL;}
            case "SMS" -> {return SMS;}
            default -> throw new CustomException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
