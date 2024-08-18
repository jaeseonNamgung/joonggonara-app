package com.hit.joonggonara.service.board;

import com.hit.joonggonara.common.custom.board.CustomFileUtil;
import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.dto.request.board.ProductRequest;
import com.hit.joonggonara.dto.response.board.ProductResponse;
import com.hit.joonggonara.entity.Product;
import com.hit.joonggonara.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {

    private final ProductRepository productRepository;
    private final CustomFileUtil fileUtil;


    public Page<ProductResponse> search(SchoolType schoolType, CategoryType categoryType, Pageable pageable) {
        return ProductResponse.fromResponse(productRepository.getSortProducts(schoolType, categoryType, pageable));
    }

    @Transactional
    public boolean upload(ProductRequest productRequest, List<MultipartFile> files) {
        Product savedProduct = productRepository.save(productRequest.toEntity());
        return fileUtil.uploadImage(savedProduct, files);
    }



}
