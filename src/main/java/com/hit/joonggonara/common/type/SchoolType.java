package com.hit.joonggonara.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SchoolType {
    /*
    HIT: 하얼빈 공업 대학교
    HJ: 흑룡강 대학교
    HC: 하얼빈 공정 대학교
    */
    ALL("전체"), HIT("하얼빈 공업 대학교"), HJ("흑룡강 대학교"), HC("하얼빈 공정 대학교");

    private final String name;

    public static SchoolType toEnum(String school) {
        return switch (school){
            case "哈尔滨工业大学" -> SchoolType.HIT;
            case "黑龙江大学" -> SchoolType.HJ;
            case "哈尔滨工程大学" -> SchoolType.HC;
            default -> SchoolType.ALL;
        };
    }
}
