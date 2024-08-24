package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.dto.request.board.ProductRequest;
import com.hit.joonggonara.dto.response.board.ProductResponse;
import com.hit.joonggonara.service.board.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BoardApiController {

    private final BoardService boardService;

    @PostMapping(path = "/board/write", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> writeBoard(@RequestPart(name = "images", required = false) List<MultipartFile> images,
                                              @RequestPart(name = "productRequest", required = false) @Valid ProductRequest productRequest,
                                              HttpServletRequest request) {
        return ResponseEntity.ok(boardService.upload(productRequest, images, request));
    }

    @GetMapping("/board/search/list")
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(name="keyword", required = false) String keyword,
            @RequestParam(name = "category") String category,
            @RequestParam(name = "school") String school, Pageable pageable) {
        return ResponseEntity.ok(boardService.search(keyword, SchoolType.toEnum(school), CategoryType.toEnum(category), pageable));

    }

    @GetMapping("/board/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(String keyword, Pageable pageable) {
        return ResponseEntity.ok(boardService.getSearchProductsByKeyword(keyword, pageable));
    }


}
