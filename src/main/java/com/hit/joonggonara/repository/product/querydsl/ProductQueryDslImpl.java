package com.hit.joonggonara.repository.product.querydsl;

import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.entity.Product;
import com.hit.joonggonara.entity.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.hit.joonggonara.entity.QMember.member;
import static com.hit.joonggonara.entity.QPhoto.photo;
import static com.hit.joonggonara.entity.QProduct.product;

@RequiredArgsConstructor
public class ProductQueryDslImpl implements ProductQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> getSortProducts(String keyword, SchoolType schoolType, CategoryType categoryType, Pageable pageable) {
        List<Product> products = queryFactory.selectFrom(product)
                .distinct()
                .leftJoin(product.member, member).fetchJoin()
                .leftJoin(product.photos, photo).fetchJoin()
                .where(keywordContain(keyword), eqSchool(schoolType),
                        eqCategory(categoryType),
                        product.member.deleted.isFalse())
                .orderBy(product.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> fetchQuery = queryFactory
                .select(product.id.count())
                .from(product)
                .where(keywordContain(keyword), eqSchool(schoolType), eqCategory(categoryType));
        return PageableExecutionUtils.getPage(products, pageable, fetchQuery::fetchOne);
    }

    @Override
    public Page<Product> findProductsByKeyword(String keyword, Pageable pageable) {
        List<Product> products = queryFactory.selectFrom(product)
                .distinct()
                .leftJoin(product.member, member).fetchJoin()
                .leftJoin(product.photos, photo).fetchJoin()
                .where(keywordContain(keyword), product.member.deleted.isFalse())
                .orderBy(product.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> fetchQuery = queryFactory
                .select(product.id.count())
                .from(product)
                .where(keywordContain(keyword));

        return PageableExecutionUtils.getPage(products, pageable, fetchQuery::fetchOne);
    }

    @Override
    public Optional<Product> findProductById(Long id) {
        Product product = queryFactory.selectFrom(QProduct.product)
                .leftJoin(QProduct.product.member, member).fetchJoin()
                .leftJoin(QProduct.product.photos,photo).fetchJoin()
                .where(QProduct.product.id.eq(id), QProduct.product.member.deleted.isFalse())
                .distinct()
                .fetchOne();
        return Optional.ofNullable(product);
    }

    @Override
    public List<Product> findByNickName(String nickName) {
        return queryFactory.selectFrom(product)
                .leftJoin(product.member, member).fetchJoin()
                .leftJoin(product.photos,photo).fetchJoin()
                .where(product.member.nickName.eq(nickName), product.member.deleted.isFalse())
                .orderBy(product.createdDate.desc())
                .distinct()
                .fetch();
    }


    private BooleanExpression keywordContain(String keyword) {
        return keyword != null ? priceContain(keyword).or(titleContain(keyword)): null;

    }


    private BooleanExpression titleContain(String title) {
        return product.title.containsIgnoreCase(title);
    }

    private BooleanExpression priceContain(String price) {
        return product.price.stringValue().containsIgnoreCase(price);
    }

    private BooleanExpression eqSchool(SchoolType schoolType) {
        return !schoolType.equals(SchoolType.ALL) ? product.schoolType.eq(schoolType) : null;
    }

    private BooleanExpression eqCategory(CategoryType categoryType) {
        return !categoryType.equals(CategoryType.ALL)? product.categoryType.eq(categoryType) : null;
    }
}

