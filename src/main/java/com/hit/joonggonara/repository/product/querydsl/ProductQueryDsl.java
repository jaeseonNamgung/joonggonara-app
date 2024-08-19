package com.hit.joonggonara.repository.product.querydsl;

import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryDsl {

    Page<Product> getSortProducts(SchoolType schoolType, CategoryType categoryType, Pageable pageable);
}