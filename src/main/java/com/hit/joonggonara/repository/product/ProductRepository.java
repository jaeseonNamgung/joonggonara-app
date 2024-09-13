package com.hit.joonggonara.repository.product;

import com.hit.joonggonara.repository.product.querydsl.ProductQueryDsl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<com.hit.joonggonara.entity.Product, Long>, ProductQueryDsl {
}
