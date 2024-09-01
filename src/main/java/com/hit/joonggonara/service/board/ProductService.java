package com.hit.joonggonara.service.board;

import com.hit.joonggonara.common.custom.board.CustomFileUtil;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.BoardErrorCode;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.type.*;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.dto.request.board.ProductRequest;
import com.hit.joonggonara.dto.response.board.ProductResponse;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.entity.Product;
import com.hit.joonggonara.repository.login.MemberRepository;
import com.hit.joonggonara.repository.login.condition.LoginCondition;
import com.hit.joonggonara.repository.product.ProductRepository;
import io.jsonwebtoken.lang.Strings;
import jakarta.servlet.http.HttpServletRequest;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final CustomFileUtil fileUtil;


    public Page<ProductResponse> search(String keyword, SchoolType schoolType, CategoryType categoryType, Pageable pageable) {
        return ProductResponse.fromResponse(productRepository.getSortProducts(keyword, schoolType, categoryType, pageable));
    }

    @Transactional
    public ProductResponse upload(ProductRequest productRequest, List<MultipartFile> files, HttpServletRequest request) {
        String accessToken = getParseJwt(request.getHeader(JwtProperties.AUTHORIZATION));
        if(!Strings.hasText(accessToken)){
            throw new CustomException(UserErrorCode.ALREADY_LOGGED_OUT_USER);
        }
        jwtUtil.validateToken(accessToken, TokenType.REFRESH_TOKEN);
        String userId = jwtUtil.getPrincipal(accessToken);
        LoginType loginType = jwtUtil.getLoginType(accessToken);
        LoginCondition loginCondition = LoginCondition.of(userId, loginType);
        Member member = memberRepository.findByPrincipalAndLoginType(loginCondition)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_EXIST_USER));
        Product savedProduct = productRepository.save(productRequest.toEntity(member));

        if(fileUtil.uploadImage(savedProduct, files)){
            return ProductResponse.fromResponse(productRepository.findProductById(savedProduct.getId())
                    .orElseThrow(() -> new CustomException(BoardErrorCode.NOT_UPLOADED_PRODUCT)));
        }

        return ProductResponse.empty();
    }

    public Page<ProductResponse> getSearchProductsByKeyword(String keyword, Pageable pageable) {
        return ProductResponse.fromResponse(productRepository.findProductsByKeyword(keyword, pageable));
    }

    private String getParseJwt(String token) {

        if(Strings.hasText(token) && token.startsWith(JwtProperties.JWT_TYPE)){
            return token.substring(7);
        }
        return null;
    }

    @Transactional
    public Boolean updateProductStatus(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(BoardErrorCode.NOT_EXIST_PRODUCT));
        product.updateIsSoldOut();
        return true;
    }

    public List<ProductResponse> getProduct(String nickName) {
        return ProductResponse.fromResponse(productRepository.findByNickName(nickName));
    }

    @Transactional
    public Boolean delete(Long productId) {
        productRepository.deleteById(productId);
        return true;
    }
}
