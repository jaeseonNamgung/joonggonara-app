package com.hit.joonggonara.common.custom.board;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.BoardErrorCode;
import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import com.hit.joonggonara.entity.Photo;
import com.hit.joonggonara.entity.Product;
import com.hit.joonggonara.repository.PhotoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CustomFileUtilTest {

    @Mock
    private PhotoRepository photoRepository;

    @InjectMocks
    private CustomFileUtil sut;



    @Test
    @DisplayName("[이미지 업로드] 이미지가 성공적으로 저장되면 true를 리턴")
    void ReturnsTrueIfTheImageIsSavedSuccessfully() throws Exception {
        //given
        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test data".getBytes()
        );
        Product product = createProduct();
        Photo photo = createPhoto(product);
        given(photoRepository.save(any())).willReturn(photo);
        //when
        boolean expectedValue = sut.uploadImage(product, List.of(validFile));
        //then
        assertThat(expectedValue).isTrue();
        then(photoRepository).should().save(any());

    }

    @Test
    @DisplayName("[이미지 업로드] 확장자가 jpg, png 가 아닐경우 에러를 던진다.")
    void ThrowMismatchExtensionExceptionIfInvalidFileType() throws Exception {
        //given
        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "test.gif",
                "image/gif",
                "test data".getBytes()
        );
        Product product = createProduct();
        //when
        CustomException expectedException =
                (CustomException) catchException(() -> sut.uploadImage(product, List.of(validFile)));
        //then
        assertThat(expectedException.getMessage()).isEqualTo(BoardErrorCode.MISMATCH_EXTENSION.getMessage());
        assertThat(expectedException.getErrorCode().getHttpStatus())
                .isEqualTo(BoardErrorCode.MISMATCH_EXTENSION.getHttpStatus());

    }

    @Test
    @DisplayName("[이미지 업로드] 이미지가 존재하지 않을 경우 에러를 던진다.")
    void ThrowNOT_UPLOADED_IMAGEExceptionIfNotExistImage() throws Exception {
        // Given
        List<MultipartFile> emptyFileList = Collections.emptyList();
        Product product = createProduct();
        // When
        CustomException exception = assertThrows(CustomException.class, () ->
                sut.uploadImage(product, emptyFileList)
        );

        // Then
        Assertions.assertEquals(BoardErrorCode.NOT_UPLOADED_IMAGE, exception.getErrorCode());
    }

    private Photo createPhoto(Product product) {
        return Photo.builder()
                .fileName("filename")
                .filePath("/filepath")
                .product(product)
                .build();
    }

    private static Product createProduct() {
        return Product.builder()
                .title("title")
                .price((long)1000)
                .categoryType(CategoryType.BOOK)
                .schoolType(SchoolType.HIT)
                .content("content")
                .isSoldOut(false)
                .productStatus("최상")
                .tradingPlace("학교 앞")
                .build();
    }

}
