package com.hit.joonggonara.common.custom.board;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.BoardErrorCode;
import com.hit.joonggonara.entity.Photo;
import com.hit.joonggonara.entity.Product;
import com.hit.joonggonara.repository.product.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CustomFileUtil {

    private final PhotoRepository photoRepository;

    public boolean uploadImage(Product product, List<MultipartFile> multipartFiles) {

        String path = "/src/main/resources/static/upload/images/";
        File file = new File(path);
        // 디렉토리가 존재하지 않는다면 디렉토리 생성
        if(!file.exists()){
            /// mkdir() 함수와 다른 점은 상위 디렉토리가 존재하지 않을 때 그것까지 생성
            file.mkdirs();
        }

        String filePath = System.getProperty("user.dir") + path;

        if(multipartFiles.isEmpty()){
            throw new CustomException(BoardErrorCode.NOT_UPLOADED_IMAGE);
        }

        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

            String contentType = multipartFile.getContentType();

            // jpg, png 확장자만 업로드 가능
            if(!contentType.equals("image/jpeg") && !contentType.equals("image/png")){
                throw new CustomException(BoardErrorCode.MISMATCH_EXTENSION);
            }

            Photo photo = Photo.builder()
                    .filePath(filePath)
                    .fileName(fileName)
                    .product(product)
                    .build();
            photoRepository.save(photo);
            photo.addProduct(product);

            file = new File(filePath + fileName);
            try {
                multipartFile.transferTo(file);
                return true;
            } catch (IOException e) {
                throw new CustomException(BoardErrorCode.IO_ERROR);
            }
        }
        return false;
    }

}
