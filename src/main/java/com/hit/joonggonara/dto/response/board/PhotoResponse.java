package com.hit.joonggonara.dto.response.board;

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

    public static List<PhotoResponse> fromResponse(List<Photo> photos){
        return photos.stream().map(photo -> PhotoResponse.of(
                photo.getId(),
                photo.getFileName(),
                photo.getFilePath()
        )).toList();
    }
}
