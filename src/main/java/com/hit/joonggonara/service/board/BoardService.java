package com.hit.joonggonara.service.board;

import com.hit.joonggonara.common.custom.board.CustomFileUtil;
import com.hit.joonggonara.common.error.CustomException;
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
public class BoardService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final CustomFileUtil fileUtil;


    public Page<ProductResponse> search(SchoolType schoolType, CategoryType categoryType, Pageable pageable) {
        return ProductResponse.fromResponse(productRepository.getSortProducts(schoolType, categoryType, pageable));
    }

    @Transactional
    public boolean upload(ProductRequest productRequest, List<MultipartFile> files, HttpServletRequest request) {
        String accessToken = getParseJwt(request.getHeader(JwtProperties.AUTHORIZATION));
        if(!Strings.hasText(accessToken)){
            throw new CustomException(UserErrorCode.ALREADY_LOGGED_OUT_USER);
        }
        jwtUtil.validateToken(accessToken, TokenType.REFRESH_TOKEN);
        String userId = jwtUtil.getPrincipal(accessToken);
        LoginType loginType = jwtUtil.getLoginType(accessToken);
        LoginCondition loginCondition = LoginCondition.of(userId, loginType);
        Member member = memberRepository.findByPrincipal(loginCondition)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_EXIST_USER));
        Product savedProduct = productRepository.save(productRequest.toEntity(member));
        return fileUtil.uploadImage(savedProduct, files);
    }

    private String getParseJwt(String token) {

        if(Strings.hasText(token) && token.startsWith(JwtProperties.JWT_TYPE)){
            return token.substring(7);
        }
        return null;
    }



}
