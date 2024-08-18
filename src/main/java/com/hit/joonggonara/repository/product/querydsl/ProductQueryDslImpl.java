package com.hit.joonggonara.repository.product.querydsl;

import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.entity.Product;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.hit.joonggonara.entity.QProduct.product;

@RequiredArgsConstructor
public class ProductQueryDslImpl implements ProductQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> getSortProducts(SchoolType schoolType, CategoryType categoryType, Pageable pageable) {
        List<Product> products = queryFactory.selectFrom(product)
                .where(eqSchool(schoolType), eqCategory(categoryType))
                .orderBy(product.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> fetchQuery = queryFactory
                .select(product.id.count())
                .from(product)
                .where(eqSchool(schoolType), eqCategory(categoryType));
        return PageableExecutionUtils.getPage(products, pageable, fetchQuery::fetchOne);
    }

    private BooleanExpression eqSchool(SchoolType schoolType) {
        return schoolType != null ? product.schoolType.eq(schoolType) : null;
    }

    private BooleanExpression eqCategory(CategoryType categoryType) {
        return categoryType != null ? product.categoryType.eq(categoryType) : null;
    }
}

