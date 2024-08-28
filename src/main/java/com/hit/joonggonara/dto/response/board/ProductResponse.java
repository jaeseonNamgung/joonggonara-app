package com.hit.joonggonara.dto.response.board;

import com.hit.joonggonara.common.type.CategoryType;
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
        String createdDate,
        List<PhotoResponse> photos,
        boolean isSoldOut,
        CategoryType categoryType,
        String school,
        MemberResponse memberResponse
) {


    public static ProductResponse of(
            Long id,
            Long price,
            String title,
            String content,
            String tradingPlace,
            String productStatus,
            String createdDate,
            List<PhotoResponse> photos,
            boolean isSoldOut,
            CategoryType categoryType,
            String school,
            MemberResponse memberResponse
    ) {
        return new ProductResponse(id, price, title, content,tradingPlace, productStatus,createdDate, photos, isSoldOut, categoryType, school, memberResponse);
    }

    public static Page<ProductResponse> fromResponse(Page<Product> products) {
        return products.map(product -> ProductResponse.of(
                product.getId(),
                product.getPrice(),
                product.getTitle(),
                product.getContent(),
                product.getTradingPlace(),
                product.getProductStatus(),
                product.getUpdatedDate().toString(),
                PhotoResponse.fromResponse(product.getPhotos()),
                product.isSoldOut(),
                product.getCategoryType(),
                product.getSchoolType().getName(),
                MemberResponse.fromResponse(product.getMember())
        ));
    }

    public static ProductResponse fromResponse(Product product) {
        return ProductResponse.of(
                product.getId(),
                product.getPrice(),
                product.getTitle(),
                product.getContent(),
                product.getTradingPlace(),
                product.getProductStatus(),
                product.getUpdatedDate().toString(),
                PhotoResponse.fromResponse(product.getPhotos()),
                product.isSoldOut(),
                product.getCategoryType(),
                product.getSchoolType().getName(),
                MemberResponse.fromResponse(product.getMember())
        );
    }
    public static List<ProductResponse> fromResponse(List<Product> products) {
        return products.stream().map(product -> ProductResponse.of(
                product.getId(),
                product.getPrice(),
                product.getTitle(),
                product.getContent(),
                product.getTradingPlace(),
                product.getProductStatus(),
                product.getUpdatedDate().toString(),
                PhotoResponse.fromResponse(product.getPhotos()),
                product.isSoldOut(),
                product.getCategoryType(),
                product.getSchoolType().getName(),
                MemberResponse.fromResponse(product.getMember())
        )).toList();
    }
    public static ProductResponse empty() {
        return ProductResponse.of(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                null
        );
    }
}
