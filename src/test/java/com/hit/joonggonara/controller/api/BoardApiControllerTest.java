package com.hit.joonggonara.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.joonggonara.common.properties.ValidationMessageProperties;
import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.dto.request.board.ProductRequest;
import com.hit.joonggonara.dto.response.board.ProductResponse;
import com.hit.joonggonara.service.board.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardApiController.class)
class BoardApiControllerTest {

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;


    @WithMockUser(roles = "USER")
    @ParameterizedTest
    @MethodSource
    @DisplayName("[Validation] ProductRequest Null 검사")
    void validationAPiExceptionTest(String title, Long price, String content, String tradingPlace,
                                    String productStatus, String message) throws Exception {
        //given
        MockMultipartFile mockFile = new MockMultipartFile(
                "images", "test.jpg", "image/jpeg", "test image content".getBytes());

        ProductRequest productRequest = ProductRequest.of(title, "Book", price, content,
                tradingPlace, productStatus, "HIT");

        MockMultipartFile productRequestJson = new MockMultipartFile(
                "productRequest", "", "application/json", objectMapper.writeValueAsBytes(productRequest));

        //when
        ResultActions resultActions = mvc.perform(multipart(HttpMethod.POST, "/board/write")
                        .file(mockFile)
                        .file(productRequestJson)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                ).andExpect(status().isBadRequest());

            resultActions
                    .andExpect(jsonPath("$.fieldErrors[*].message").value(message));
        //then

    }

    @WithMockUser("USER")
    @Test
    @DisplayName("[API] 상품 등록 컨트롤러 테스트 성공 시 Response 를 리턴")
    void TestProductRegistrationController() throws Exception {
        //given
        ProductResponse productResponse = createProductResponse();
        MockMultipartFile mockFile = new MockMultipartFile(
                "images", "test.jpg", "image/jpeg", "test image content".getBytes());

        ProductRequest productRequest = ProductRequest.of("title", "책", (long)10000, "content",
                "학교앞", "최상", "HIT");
        MockMultipartFile productRequestJson = new MockMultipartFile(
                "productRequest", "", "application/json", objectMapper.writeValueAsBytes(productRequest));
        given(productService.upload(any(), any(), any())).willReturn(productResponse);

        //when & then
        mvc.perform(multipart(HttpMethod.POST, "/board/write")
                .file(mockFile)
                .file(productRequestJson)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", "token")
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.categoryType").value(CategoryType.BOOK.name()))
                .andExpect(jsonPath("$.tradingPlace").value("하공대 정문 앞"));
        then(productService).should().upload(any(), any(), any());

    }

    @WithMockUser("USER")
    @Test
    @DisplayName("[API] 카테고리 및 학교 별로 상품 목록 조회")
    void TestProductSearchByCategoryAndSchool() throws Exception {
        //given
        ProductResponse productResponse = createProductResponse();
        given(productService.search(any(),any(),any(), any())).willReturn(new PageImpl<>(List.of(productResponse)));
        //when & then
        mvc.perform(get("/board/search")
                        .queryParam("category", CategoryType.BOOK.name())
                        .queryParam("school", SchoolType.HIT.name())
                        .queryParam("size", "5")
                        .queryParam("page", "1")
                        .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("title"))
                .andExpect(jsonPath("$.content[0].content").value("content"))
                .andExpect(jsonPath("$.content[0].categoryType").value("BOOK"))
                .andExpect(jsonPath("$.content[0].schoolType").value("HIT"))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.number").value(0));
        then(productService).should().search(any(),any(),any(), any());

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


    static Stream<Arguments> validationAPiExceptionTest(){
        return Stream.of(
                Arguments.of("", 1000L, "content", "HIT", "죄상", ValidationMessageProperties.TITLE_NOT_BLANK),
                Arguments.of("title", null, "content", "HIT", "죄상", ValidationMessageProperties.PRICE_NOT_BLANK),
                Arguments.of("title", 1000L, "", "HIT", "죄상", ValidationMessageProperties.CONTENT_NOT_BLANK),
                Arguments.of("title", 1000L, "content", "", "죄상", ValidationMessageProperties.TRADING_PLACE_NOT_BLANK),
                Arguments.of("title", 1000L, "content", "HIT", "", ValidationMessageProperties.PRODUCT_STATUS_NOT_BLANK)
        );
    }

}
