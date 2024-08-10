package com.hit.joonggonara.common.type;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.BaseErrorCode;

public enum AuthenticationType {
    ID, PASSWORD;

    public static AuthenticationType toEnum(String authenticationType){
        authenticationType  = authenticationType.toUpperCase();

        switch (authenticationType){
            case "ID" -> {return AuthenticationType.ID;}
            case "PASSWORD" -> {return AuthenticationType.PASSWORD;}
            default -> throw new CustomException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
