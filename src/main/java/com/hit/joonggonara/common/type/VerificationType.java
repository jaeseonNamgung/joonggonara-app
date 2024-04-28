package com.hit.joonggonara.common.type;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.BaseErrorCode;

public enum VerificationType {
    ID_EMAIL, ID_SMS, PASSWORD_EMAIL, PASSWORD_SMS, EMAIL, SMS;

    public static VerificationType toEnum(String verificationType){

        String upperCase = verificationType.toUpperCase();

        if(upperCase.equals("EMAIL")){
            return EMAIL;
        }else if(upperCase.equals("SMS")){
            return SMS;
        }

        throw new CustomException(BaseErrorCode.INTERNAL_SERVER_ERROR);
    }
}
