package com.hit.joonggonara.repository.product.querydsl;

import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductQueryDsl {

    Page<Product> getSortProducts(String keyword, SchoolType schoolType, CategoryType categoryType, Pageable pageable);
    Page<Product> findProductsByKeyword(String keyword, Pageable pageable);
    Optional<Product> findProductById(Long id);
    List<Product> findByNickName(String nickName);
}
