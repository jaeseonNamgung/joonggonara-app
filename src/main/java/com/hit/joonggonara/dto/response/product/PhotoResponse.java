package com.hit.joonggonara.dto.response.product;

import com.hit.joonggonara.entity.CommunityImage;
import com.hit.joonggonara.entity.Photo;

import java.util.List;

public record PhotoResponse(
        Long id,
        String fileName,
        String filePath

) {
    public static PhotoResponse of(
            Long id,
            String fileName,
            String filePath

    ) {
        return new PhotoResponse(id, fileName, filePath);
    }

    public static List<PhotoResponse> fromPhotoResponse(List<Photo> photos){
        return photos.stream().map(photo -> PhotoResponse.of(
                photo.getId(),
                photo.getFileName(),
                photo.getFilePath()
        )).toList();
    }

    public static List<PhotoResponse> fromCommunityImageResponse(List<CommunityImage> images) {
        return images.stream().map(image -> PhotoResponse.of(
                image.getId(),
                image.getFileName(),
                image.getFilePath()
        )).toList();
    }
}
