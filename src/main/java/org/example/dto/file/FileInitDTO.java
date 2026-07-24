package org.example.dto.file;

import lombok.Data;

@Data
public class FileInitDTO {

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String md5;

    private Integer totalChunks;
}