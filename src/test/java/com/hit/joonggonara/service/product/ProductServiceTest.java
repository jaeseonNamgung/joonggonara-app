package com.hit.joonggonara.service.product;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.ProductErrorCode;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.common.util.CustomFileUtil;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.dto.file.FileDto;
import com.hit.joonggonara.dto.request.product.ProductRequest;
import com.hit.joonggonara.dto.response.product.ProductResponse;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.entity.Photo;
import com.hit.joonggonara.entity.Product;
import com.hit.joonggonara.repository.login.MemberRepository;
import com.hit.joonggonara.repository.product.PhotoRepository;
import com.hit.joonggonara.repository.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PhotoRepository photoRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CustomFileUtil customFileUtil;

    @InjectMocks
    private ProductService sut;

    @Test
    @DisplayName("[Service] 성공적으로 글을 업로드 할 경우 response 를 리턴")
    void returnsResponseIfThePostIsSuccessfullyUploaded() throws Exception {
        //given
        ProductRequest productRequest = createProductRequest();
        Product product = createProduct();
        MultipartFile multipartFile = new MockMultipartFile(
                "file", "test.png", "image/png", "test data".getBytes());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtProperties.AUTHORIZATION, JwtProperties.JWT_TYPE + "token");
        Member member = createMember();
        FileDto fileDto = FileDto.of("/filePath/", "test.png");
        ReflectionTestUtils.setField(product, "updatedDate", LocalDateTime.now());
        ReflectionTestUtils.setField(product, "id", 1L);
        Photo photo = Photo.builder().filePath("/filePath/").fileName("test.png").product(product).build();
        given(jwtUtil.getPrincipal(any())).willReturn("userId");
        given(jwtUtil.getLoginType(any())).willReturn(LoginType.GENERAL);
        given(memberRepository.findByPrincipalAndLoginType(any())).willReturn(Optional.of(member));
        given(productRepository.save(any())).willReturn(product);
        given(customFileUtil.uploadImage(any())).willReturn(List.of(fileDto));
        given(photoRepository.save(any())).willReturn(photo);
        //when
        ProductResponse expectedResponse = sut.upload(productRequest, List.of(multipartFile), request);
        //then
        assertThat(expectedResponse.title()).isEqualTo("title");
        assertThat(expectedResponse.content()).isEqualTo("content");
        assertThat(expectedResponse.categoryType()).isEqualTo(CategoryType.BOOK);
        assertThat(expectedResponse.tradingPlace()).isEqualTo("하공대 정문 앞");
        assertThat(expectedResponse.photos()).isNotEmpty();
        assertThat(expectedResponse.memberResponse()).isNotNull();
        then(productRepository).should().save(any());
        then(customFileUtil).should().uploadImage(any());
        then(jwtUtil).should().getPrincipal(any());
        then(jwtUtil).should().getLoginType(any());
        then(memberRepository).should().findByPrincipalAndLoginType(any());
        then(photoRepository).should().save(any());

    }

    @Test
    @DisplayName("[Service] 상품 이미지가 존재하지 않을 경우 NOT_UPLOADED_IMAGE 에러를 던진다.")
    void throwsNOT_UPLOADED_IMAGEIfNotExistImage() throws Exception {
        //given
        ProductRequest productRequest = createProductRequest();
        Product product = createProduct();
        MultipartFile multipartFile = new MockMultipartFile(
                "file", "test.png", "image/png", "test data".getBytes());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtProperties.AUTHORIZATION, JwtProperties.JWT_TYPE + "token");
        Member member = createMember();

        ReflectionTestUtils.setField(product, "updatedDate", LocalDateTime.now());

        given(jwtUtil.getPrincipal(any())).willReturn("userId");
        given(jwtUtil.getLoginType(any())).willReturn(LoginType.GENERAL);
        given(memberRepository.findByPrincipalAndLoginType(any())).willReturn(Optional.of(member));
        given(productRepository.save(any())).willReturn(product);
        given(customFileUtil.uploadImage(any())).willReturn(List.of());

        //when
        CustomException expectedException = assertThrows(CustomException.class,
                () -> sut.upload(productRequest, List.of(multipartFile), request));
        //then
        assertThat(expectedException.getErrorCode()).isEqualTo(ProductErrorCode.NOT_UPLOADED_IMAGE);
        assertThat(expectedException).hasMessage(ProductErrorCode.NOT_UPLOADED_IMAGE.getMessage());

        then(jwtUtil).should().getPrincipal(any());
        then(jwtUtil).should().getLoginType(any());
        then(memberRepository).should().findByPrincipalAndLoginType(any());
        then(productRepository).should().save(any());
        then(customFileUtil).should().uploadImage(any());

    }

    @Test
    @DisplayName("[Service] 토큰이 null일 겨우 ALREADY_LOGGED_OUT_USER 에러를 던진다.")
    void throwALREADY_LOGGED_OUT_USERErrorIfTokenIsNull() throws Exception {
        //given
        ProductRequest productRequest = createProductRequest();
        MultipartFile multipartFile = new MockMultipartFile(
                "file", "test.png", "image/png", "test data".getBytes());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtProperties.AUTHORIZATION, "");
        Member member = createMember();
        //when
        CustomException expectedException = assertThrows(CustomException.class,
                () -> sut.upload(productRequest, List.of(multipartFile), request));
        //then
        assertThat(expectedException.getErrorCode()).isEqualTo(UserErrorCode.ALREADY_LOGGED_OUT_USER);
        assertThat(expectedException).hasMessage(UserErrorCode.ALREADY_LOGGED_OUT_USER.getMessage());

    }

    @Test
    @DisplayName("[Service] 회원이 존재하지 않을 경우 NOT_EXIST_USER 에러를 던진다.")
    void throwNOT_EXIST_USERErrorIfNotExistMember() throws Exception {
        //given
        ProductRequest productRequest = createProductRequest();
        MultipartFile multipartFile = new MockMultipartFile(
                "file", "test.png", "image/png", "test data".getBytes());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtProperties.AUTHORIZATION, JwtProperties.JWT_TYPE + "token");
        given(jwtUtil.getPrincipal(any())).willReturn("userId");
        given(jwtUtil.getLoginType(any())).willReturn(LoginType.GENERAL);
        given(memberRepository.findByPrincipalAndLoginType(any())).willReturn(Optional.empty());

        //when
        CustomException expectedException = assertThrows(CustomException.class,
                () -> sut.upload(productRequest, List.of(multipartFile), request));
        //then
        assertThat(expectedException.getErrorCode()).isEqualTo(UserErrorCode.NOT_EXIST_USER);
        assertThat(expectedException).hasMessage(UserErrorCode.NOT_EXIST_USER.getMessage());

        then(jwtUtil).should().getPrincipal(any());
        then(jwtUtil).should().getLoginType(any());
        then(memberRepository).should().findByPrincipalAndLoginType(any());


    }

    private Member createMember() {
        return Member.builder()
                .name("name")
                .email("email")
                .build();
    }

    @Test
    @DisplayName("[Service] 학교 및 카데고리로 조회한 상품이 존재할 경우 페이징 처리된 상품을 리턴")
    void returnProductsPagingIfExistProducts() throws Exception {
        //given
        String keyword = "keyword";
        PageRequest pageRequest = PageRequest.of( 1, 5);
        Product product = createProduct();
        ReflectionTestUtils.setField(product, "updatedDate", LocalDateTime.now());
        given(productRepository.getSortProducts(any(), any(), any(), any()))
                .willReturn(new PageImpl<>(List.of(product)));

        //when
        Page<ProductResponse> expectedPage = sut.search(keyword, SchoolType.HIT, CategoryType.BOOK, pageRequest);

        System.out.println(expectedPage.getTotalPages());
        //then
        assertThat(expectedPage.getContent().get(0).categoryType()).isEqualTo(CategoryType.BOOK);
        assertThat(expectedPage.getContent().get(0).school()).isEqualTo(SchoolType.HIT.getName());
        assertThat(expectedPage.getSize()).isEqualTo(1);
        assertThat(expectedPage.getTotalPages()).isEqualTo(1);
        assertThat(expectedPage.isFirst()).isTrue();

        then(productRepository).should().getSortProducts(any(), any(), any(), any());

    }

    @Test
    @DisplayName("[Service] 학교 및 카데고리로 조회한 상품이 존재하지 않을 경우 null을 반환")
    void returnsNullIfNotExistProducts() throws Exception {
        //given
        String keyword = "keyword";
        PageRequest pageRequest = PageRequest.of( 1, 5);
        Product product = createProduct();
        given(productRepository.getSortProducts(any(), any(), any(), any()))
                .willReturn(new PageImpl<>(List.of()));
        //when
        Page<ProductResponse> expectedPage = sut.search(keyword, SchoolType.HIT, CategoryType.BOOK, pageRequest);

        //then
        assertThat(expectedPage).isEmpty();
        assertThat(expectedPage.getSize()).isEqualTo(0);
        assertThat(expectedPage.getTotalPages()).isEqualTo(1);

        then(productRepository).should().getSortProducts(any(), any(), any(), any());

    }

    private Product createProduct() {
        return Product.builder()
                .title("title")
                .price((long)10000)
                .content("content")
                .categoryType(CategoryType.BOOK)
                .schoolType(SchoolType.HIT)
                .tradingPlace("하공대 정문 앞")
                .productStatus("최상")
                .member(Member.builder().email("email").name("name").build())
                .build();
    }

    private ProductRequest createProductRequest() {
        return ProductRequest.of(
                "title",
                "BOOK",
                (long)10000,
                "content",
                "하공대 정문 앞",
                "최상",
                "HIT"
        );
    }

    private ProductResponse createProductResponse() {
        return ProductResponse.of(
                1L,
                (long)10000,
                "title",
                "content",
                "하공대 정문 앞",
                "최상",
                LocalDateTime.now().toString(),
                null,
                false,
                CategoryType.BOOK,
                SchoolType.HIT.getName(),
                null
        );
    }



}
