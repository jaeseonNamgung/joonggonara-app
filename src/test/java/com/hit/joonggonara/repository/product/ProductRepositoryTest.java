package com.hit.joonggonara.repository.product;


import com.hit.joonggonara.common.config.JPAConfig;
import com.hit.joonggonara.common.config.P6SpyConfig;
import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:/application.yaml")
@Import({JPAConfig.class, P6SpyConfig.class})
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("[JPA][Query Dsl] 학교 이름과 카테고리로 가장 최근 날짜 순으로 조회 ")
    void givenSchoolTypeAndPageable_whenGetSortProductsBySchool_thenReturnsSortedProducts() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 3);
        for (int i = 1; i <= 5 ; i++) {
            productRepository.save(createProduct(i, SchoolType.HIT, CategoryType.BOOK));
            productRepository.save(createProduct(i, SchoolType.HC, CategoryType.CLOTHING));
            productRepository.save(createProduct(i, SchoolType.HJ, CategoryType.COSMETICS));
        }
        //when
        Page<Product> expectedProducts = productRepository.getSortProducts(SchoolType.HIT, CategoryType.BOOK, pageable);
        //then
        assertThat(expectedProducts).hasSize(3);
        assertThat(expectedProducts.getTotalPages()).isEqualTo(2);
        assertThat(expectedProducts.isFirst()).isTrue();
        assertThat(expectedProducts.getTotalElements()).isEqualTo(5);
        assertThat(expectedProducts.getContent().get(0).getTitle()).isEqualTo("title5");
        assertThat(expectedProducts.getContent().get(0).getCategoryType().name()).isEqualTo("BOOK");
        assertThat(expectedProducts.getContent().get(0).getSchoolType().name()).isEqualTo("HIT");
        assertThat(expectedProducts.getContent().get(1).getTitle()).isEqualTo("title4");
        assertThat(expectedProducts.getContent().get(1).getCategoryType().name()).isEqualTo("BOOK");
        assertThat(expectedProducts.getContent().get(1).getSchoolType().name()).isEqualTo("HIT");
        assertThat(expectedProducts.getContent().get(2).getTitle()).isEqualTo("title3");
        assertThat(expectedProducts.getContent().get(2).getCategoryType().name()).isEqualTo("BOOK");
        assertThat(expectedProducts.getContent().get(2).getSchoolType().name()).isEqualTo("HIT");


    }

    private static Product createProduct(int i, SchoolType schoolType, CategoryType categoryType) {
        return Product.builder()
                .title("title"+i)
                .price((long) i)
                .categoryType(categoryType)
                .content("content"+i)
                .isSoldOut(false)
                .productStatus("최상")
                .tradingPlace("하공대 정문 앞")
                .schoolType(schoolType)
                .build();
    }


}
