package com.hit.joonggonara.common.util;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.ProductErrorCode;
import com.hit.joonggonara.dto.file.FileDto;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CustomFileUtil {

    private final String PATH = "/src/main/resources/static/upload/images/";

    public List<FileDto> uploadImage(List<MultipartFile> multipartFiles) {

        List<FileDto> fileDtoList = new ArrayList<>();
        File file = new File(PATH);
        // 디렉토리가 존재하지 않는다면 디렉토리 생성
        if(!file.exists()){
            /// mkdir() 함수와 다른 점은 상위 디렉토리가 존재하지 않을 때 그것까지 생성
            file.mkdirs();
        }

        String filePath = System.getProperty("user.dir") + PATH;

        if(multipartFiles.isEmpty()){
            throw new CustomException(ProductErrorCode.NOT_UPLOADED_IMAGE);
        }
        String fileName = "";
        for (MultipartFile multipartFile : multipartFiles) {
            fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

            String contentType = multipartFile.getContentType();

            // jpg, png 확장자만 업로드 가능
            assert contentType != null;
            if(!contentType.equals("image/jpeg") && !contentType.equals("image/png")){
                throw new CustomException(ProductErrorCode.MISMATCH_EXTENSION);
            }

            file = new File(filePath + fileName);
            try {
                multipartFile.transferTo(file);
                fileDtoList.add(FileDto.of(filePath, fileName));
            } catch (IOException e) {
                throw new CustomException(ProductErrorCode.IO_ERROR);
            }
        }
       return fileDtoList;
    }

    public String uploadProfile(MultipartFile profile) {
        File file = new File(PATH);
        // 디렉토리가 존재하지 않는다면 디렉토리 생성
        if(!file.exists()){
            /// mkdir() 함수와 다른 점은 상위 디렉토리가 존재하지 않을 때 그것까지 생성
            file.mkdirs();
        }
        String filePath = System.getProperty("user.dir") + PATH;

        if(profile.isEmpty()){
            throw new CustomException(ProductErrorCode.NOT_UPLOADED_IMAGE);
        }

        String fileName = UUID.randomUUID() + "_" + profile.getOriginalFilename();
        String contentType = profile.getContentType();

        assert contentType != null;
        if(!contentType.equals("image/jpeg") && !contentType.equals("image/png")){
            throw new CustomException(ProductErrorCode.MISMATCH_EXTENSION);
        }

        file = new File(filePath + fileName);
        try {
            profile.transferTo(file);
        } catch (IOException e) {
            throw new CustomException(ProductErrorCode.IO_ERROR);
        }
        return filePath + fileName;
    }
}
