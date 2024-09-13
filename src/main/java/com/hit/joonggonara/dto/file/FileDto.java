package com.hit.joonggonara.dto.file;

public record FileDto(
        String filePath,
        String fileName
) {
    public static FileDto of(String filePath, String fileName) {
        return new FileDto(filePath, fileName);
    }
}
