package com.hit.joonggonara.dto.response.board;

import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public record ProductResponse(
        Long id,
        Long price,
        String title,
        String content,
        String tradingPlace,
        String productStatus,
        List<PhotoResponse> photos,
        boolean isSoldOut,
        CategoryType categoryType,
        SchoolType schoolType,
        MemberResponse memberResponse
) {


    public static ProductResponse of(
            Long id,
            Long price,
            String title,
            String content,
            String tradingPlace,
            String productStatus,
            List<PhotoResponse> photos,
            boolean isSoldOut,
            CategoryType categoryType,
            SchoolType schoolType,
            MemberResponse memberResponse
    ) {
        return new ProductResponse(id, price, title, content,tradingPlace, productStatus, photos, isSoldOut, categoryType, schoolType, memberResponse);
    }

    public static Page<ProductResponse> fromResponse(Page<Product> products) {
        return products.map(product -> ProductResponse.of(
                product.getId(),
                product.getPrice(),
                product.getTitle(),
                product.getContent(),
                product.getTradingPlace(),
                product.getProductStatus(),
                PhotoResponse.fromResponse(product.getPhotos()),
                product.isSoldOut(),
                product.getCategoryType(),
                product.getSchoolType(),
                MemberResponse.fromResponse(product.getMember())
        ));
    }



}
