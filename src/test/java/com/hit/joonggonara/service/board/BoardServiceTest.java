package com.hit.joonggonara.service.board;

import com.hit.joonggonara.common.custom.board.CustomFileUtil;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.dto.request.board.ProductRequest;
import com.hit.joonggonara.dto.response.board.ProductResponse;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.entity.Product;
import com.hit.joonggonara.repository.login.MemberRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CustomFileUtil customFileUtil;

    @InjectMocks
    private BoardService sut;

    @Test
    @DisplayName("[Service] 성공적으로 글을 업로드 할 경우 true를 리턴")
    void returnsTrueIfThePostIsSuccessfullyUploaded() throws Exception {
        //given
        ProductRequest productRequest = createProductRequest();
        MultipartFile multipartFile = new MockMultipartFile(
                "file", "test.png", "image/png", "test data".getBytes());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtProperties.AUTHORIZATION, JwtProperties.JWT_TYPE + "token");
        Member member = createMember();
        given(jwtUtil.getPrincipal(any())).willReturn("userId");
        given(jwtUtil.getLoginType(any())).willReturn(LoginType.GENERAL);
        given(memberRepository.findByPrincipal(any())).willReturn(Optional.of(member));
        given(productRepository.save(any())).willReturn(productRequest.toEntity(member));
        given(customFileUtil.uploadImage(any(), any())).willReturn(true);
        //when
        boolean expectedValue = sut.upload(productRequest, List.of(multipartFile), request);
        //then
        assertThat(expectedValue).isTrue();
        then(productRepository).should().save(any());
        then(customFileUtil).should().uploadImage(any(),any());
        then(jwtUtil).should().getPrincipal(any());
        then(jwtUtil).should().getLoginType(any());
        then(memberRepository).should().findByPrincipal(any());

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
        given(memberRepository.findByPrincipal(any())).willReturn(Optional.empty());

        //when
        CustomException expectedException = assertThrows(CustomException.class,
                () -> sut.upload(productRequest, List.of(multipartFile), request));
        //then
        assertThat(expectedException.getErrorCode()).isEqualTo(UserErrorCode.NOT_EXIST_USER);
        assertThat(expectedException).hasMessage(UserErrorCode.NOT_EXIST_USER.getMessage());

        then(jwtUtil).should().getPrincipal(any());
        then(jwtUtil).should().getLoginType(any());
        then(memberRepository).should().findByPrincipal(any());


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
        PageRequest pageRequest = PageRequest.of( 1, 5);
        Product product = createProduct();
        given(productRepository.getSortProducts(any(), any(), any()))
                .willReturn(new PageImpl<>(List.of(product)));
        //when
        Page<ProductResponse> expectedPage = sut.search(SchoolType.HIT, CategoryType.BOOK, pageRequest);

        System.out.println(expectedPage.getTotalPages());
        //then
        assertThat(expectedPage.getContent().get(0).categoryType()).isEqualTo(CategoryType.BOOK);
        assertThat(expectedPage.getContent().get(0).schoolType()).isEqualTo(SchoolType.HIT);
        assertThat(expectedPage.getSize()).isEqualTo(1);
        assertThat(expectedPage.getTotalPages()).isEqualTo(1);
        assertThat(expectedPage.isFirst()).isTrue();

        then(productRepository).should().getSortProducts(any(), any(), any());

    }

    @Test
    @DisplayName("[Service] 학교 및 카데고리로 조회한 상품이 존재하지 않을 경우 null을 반환")
    void returnsNullIfNotExistProducts() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of( 1, 5);
        Product product = createProduct();
        given(productRepository.getSortProducts(any(), any(), any()))
                .willReturn(new PageImpl<>(List.of()));
        //when
        Page<ProductResponse> expectedPage = sut.search(SchoolType.HIT, CategoryType.BOOK, pageRequest);

        //then
        assertThat(expectedPage).isEmpty();
        assertThat(expectedPage.getSize()).isEqualTo(0);
        assertThat(expectedPage.getTotalPages()).isEqualTo(1);

        then(productRepository).should().getSortProducts(any(), any(), any());

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





}
