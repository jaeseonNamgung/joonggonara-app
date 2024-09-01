package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.dto.request.board.ProductRequest;
import com.hit.joonggonara.dto.response.board.ProductResponse;
import com.hit.joonggonara.service.board.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BoardApiController {

    private final ProductService productService;

    @PostMapping(path = "/board/write", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> writeBoard(@RequestPart(name = "images") List<MultipartFile> images,
                                              @RequestPart(name = "productRequest") @Valid ProductRequest productRequest,
                                              HttpServletRequest request) {
        return ResponseEntity.ok(productService.upload(productRequest, images, request));
    }

    @GetMapping("/board/search/list")
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(name="keyword", required = false) String keyword,
            @RequestParam(name = "category") String category,
            @RequestParam(name = "school") String school, Pageable pageable) {
        return ResponseEntity.ok(productService.search(keyword, SchoolType.toEnum(school), CategoryType.toEnum(category), pageable));

    }

    @GetMapping("/board/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(String keyword, Pageable pageable) {
        return ResponseEntity.ok(productService.getSearchProductsByKeyword(keyword, pageable));
    }

    @MessageMapping("/product/{productId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/sub/product/{productId}") //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public ResponseEntity<Boolean> updateProductStatus(@DestinationVariable("productId") Long productId) {
        return ResponseEntity.ok(productService.updateProductStatus(productId));
    }

}
