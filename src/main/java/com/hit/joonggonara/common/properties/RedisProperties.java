package com.hit.joonggonara.common.properties;


public class RedisProperties {

    public static final String EMAIL_KEY = "principal:";
    public static final String PHONE_NUMBER_KEY = "phone_number:";
    public static final String REFRESH_TOKEN_KEY = "refresh_token:";
    public static final String BLACK_LIST_VALUE = "black_list";

    public static final Integer REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 14;
    public static final Integer EMAIL_EXPIRATION_TIME = 1800;

    // 3분
    public static final Integer PHONE_NUMBER_EXPIRATION_TIME = 1800;

}
