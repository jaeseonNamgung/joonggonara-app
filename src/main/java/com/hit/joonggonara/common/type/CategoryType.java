package com.hit.joonggonara.common.type;

public enum CategoryType {
    ALL, BOOK, CLOTHING,COSMETICS, DAILY_NECESSITY,ElECTRONIC_DEVICE, SHARING, OTHER;

    public static CategoryType toEnum(String category) {

        return switch (category){
            case "의류" -> CategoryType.CLOTHING;
            case "도서" -> CategoryType.BOOK;
            case "생활용품" -> CategoryType.DAILY_NECESSITY;
            case "화장품" -> CategoryType.COSMETICS;
            case "전자기기" -> CategoryType.ElECTRONIC_DEVICE;
            case "나눔" -> CategoryType.SHARING;
            case "기타" -> CategoryType.OTHER;
            default -> CategoryType.ALL;
        };
    }
}
