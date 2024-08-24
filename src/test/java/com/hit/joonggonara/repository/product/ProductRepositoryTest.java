package com.hit.joonggonara.repository.product;


import com.hit.joonggonara.common.config.JPAConfig;
import com.hit.joonggonara.common.config.P6SpyConfig;
import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.entity.Photo;
import com.hit.joonggonara.entity.Product;
import com.hit.joonggonara.repository.login.MemberRepository;
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
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PhotoRepository photoRepository;

    @Test
    @DisplayName("[JPA][Query Dsl] 키워드 없이 학교 이름과 카테고리로 가장 최근 날짜 순으로 조회 ")
    void givenSchoolTypeAndPageable_whenGetSortProductsBySchool_thenReturnsSortedProducts() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 3);
        Member savedMember = memberRepository.save(createMember());

        for (int i = 1; i <= 5 ; i++) {
            Photo photo1 = createPhoto(productRepository.save(createProduct(i, SchoolType.HIT, CategoryType.BOOK, savedMember)));
            Photo photo2 = createPhoto(productRepository.save(createProduct(i, SchoolType.HC, CategoryType.CLOTHING, savedMember)));
            Photo photo3 = createPhoto(productRepository.save(createProduct(i, SchoolType.HJ, CategoryType.COSMETICS, savedMember)));

            photoRepository.save(photo1);
            photoRepository.save(photo2);
            photoRepository.save(photo3);
        }
        //when
        Page<Product> expectedProducts = productRepository.getSortProducts(null, SchoolType.HIT, CategoryType.BOOK, pageable);
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

    @Test
    @DisplayName("[QueryDsl] title 키워드로 상품 검색")
    void searchProductByTitleKeywordTest() throws Exception {
        //given
        String keyword = "le1";
        Pageable pageable = PageRequest.of(0, 3);
        Member savedMember = memberRepository.save(createMember());

        for (int i = 1; i <= 5 ; i++) {
            Photo photo1 = createPhoto(productRepository.save(createProduct(i, SchoolType.HIT, CategoryType.BOOK, savedMember)));
            Photo photo2 = createPhoto(productRepository.save(createProduct(i, SchoolType.HC, CategoryType.CLOTHING, savedMember)));
            Photo photo3 = createPhoto(productRepository.save(createProduct(i, SchoolType.HJ, CategoryType.COSMETICS, savedMember)));

            photoRepository.save(photo1);
            photoRepository.save(photo2);
            photoRepository.save(photo3);
        }

        //when
        Page<Product> expectedProducts = productRepository.getSortProducts(keyword, SchoolType.ALL, CategoryType.ALL, pageable);
        //then
        assertThat(expectedProducts).hasSize(3);
        assertThat(expectedProducts.getTotalPages()).isEqualTo(1);
        assertThat(expectedProducts.isFirst()).isTrue();
        assertThat(expectedProducts.getTotalElements()).isEqualTo(3);
        assertThat(expectedProducts.getContent().get(0).getTitle()).isEqualTo("title1");


    }

    @Test
    @DisplayName("[QueryDsl] 가격 키워드로 상품 검색")
    void searchProductByContentKeywordTest() throws Exception {
        //given
        String keyword = "2";
        Pageable pageable = PageRequest.of(0, 3);
        Member savedMember = memberRepository.save(createMember());

        for (int i = 1; i <= 5 ; i++) {
            Photo photo1 = createPhoto(productRepository.save(createProduct(i, SchoolType.HIT, CategoryType.BOOK, savedMember)));
            Photo photo2 = createPhoto(productRepository.save(createProduct(i+1, SchoolType.HC, CategoryType.CLOTHING, savedMember)));
            Photo photo3 = createPhoto(productRepository.save(createProduct(i+1, SchoolType.HJ, CategoryType.COSMETICS, savedMember)));

            photoRepository.save(photo1);
            photoRepository.save(photo2);
            photoRepository.save(photo3);
        }

        //when
        Page<Product> expectedProducts = productRepository.getSortProducts(keyword, SchoolType.ALL, CategoryType.ALL, pageable);
        //then
        assertThat(expectedProducts).hasSize(3);
        assertThat(expectedProducts.getTotalPages()).isEqualTo(1);
        assertThat(expectedProducts.isFirst()).isTrue();
        assertThat(expectedProducts.getTotalElements()).isEqualTo(3);
        assertThat(expectedProducts.getContent().get(0).getPrice()).isEqualTo(2);


    }

    @Test
    @DisplayName("[QueryDsl] 제목 키워드와 카데고리, 학교 별로 상품 검색")
    void searchProductByTitleKeywordAndCategoryAndSchoolTest() throws Exception {
        //given
        String keyword = "le2";
        Pageable pageable = PageRequest.of(0, 3);
        Member savedMember = memberRepository.save(createMember());

        for (int i = 1; i <= 5 ; i++) {
            Photo photo1 = createPhoto(productRepository.save(createProduct(i, SchoolType.HIT, CategoryType.BOOK, savedMember)));
            Photo photo2 = createPhoto(productRepository.save(createProduct(i+1, SchoolType.HC, CategoryType.CLOTHING, savedMember)));
            Photo photo3 = createPhoto(productRepository.save(createProduct(i+1, SchoolType.HJ, CategoryType.COSMETICS, savedMember)));

            photoRepository.save(photo1);
            photoRepository.save(photo2);
            photoRepository.save(photo3);
        }

        //when
        Page<Product> expectedProducts = productRepository.getSortProducts(keyword, SchoolType.HIT, CategoryType.BOOK, pageable);
        //then
        assertThat(expectedProducts).hasSize(1);
        assertThat(expectedProducts.getTotalPages()).isEqualTo(1);
        assertThat(expectedProducts.isFirst()).isTrue();
        assertThat(expectedProducts.getTotalElements()).isEqualTo(1);
        assertThat(expectedProducts.getContent().get(0).getTitle()).isEqualTo("title2");
        assertThat(expectedProducts.getContent().get(0).getSchoolType()).isEqualTo(SchoolType.HIT);
        assertThat(expectedProducts.getContent().get(0).getCategoryType()).isEqualTo(CategoryType.BOOK);


    }
    @Test
    @DisplayName("[QueryDsl] 상품 아이디로 상품 조회")
    void findProductByIdTest() throws Exception {
        //given
        Member savedMember = memberRepository.save(createMember());
        Product savedProduct = productRepository.save(createProduct(1, SchoolType.HC, CategoryType.CLOTHING, savedMember));

        Photo photo1 = createPhoto(savedProduct);
        Photo photo2 = createPhoto(savedProduct);
        Photo photo3 = createPhoto(savedProduct);

        photoRepository.save(photo1);
        photoRepository.save(photo2);
        photoRepository.save(photo3);

        Photo photo = photoRepository.findById(savedProduct.getId()).get();
        //when
        Product expectedProduct = productRepository.findProductById(savedProduct.getId()).get();

        //then
        assertThat(expectedProduct.getTitle()).isEqualTo("title1");
        assertThat(expectedProduct.getSchoolType()).isEqualTo(SchoolType.HC);
        assertThat(expectedProduct.getCategoryType()).isEqualTo(CategoryType.CLOTHING);
        assertThat(expectedProduct.getPhotos().size()).isEqualTo(3);
    }

    private Photo createPhoto(Product product) {
        return Photo.builder().fileName("fileName").filePath("filePath").product(product).build();
    }

    private Member createMember() {
        return Member.builder()
                .userId("testId")
                .email("test@email.com")
                .name("hong")
                .nickName("nickName")
                .password("Abc1234*")
                .phoneNumber("+8612345678")
                .role(Role.ROLE_USER)
                .loginType(LoginType.GENERAL)
                .build();
    }

    private static Product createProduct(int i, SchoolType schoolType, CategoryType categoryType, Member member) {
        return Product.builder()
                .title("title"+i)
                .price((long) i)
                .categoryType(categoryType)
                .content("content"+i)
                .isSoldOut(false)
                .productStatus("최상")
                .tradingPlace("하공대 정문 앞")
                .schoolType(schoolType)
                .member(member)
                .build();
    }


}
