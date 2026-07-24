package org.example.service;

import org.example.dto.file.FileInitDTO;
import org.example.dto.file.FileInitResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {

    FileInitResponseDTO initUpload(Long userId, FileInitDTO dto);

    void uploadChunk(String uploadId, Integer chunkIndex, MultipartFile file);

    Map<String, String> mergeChunks(String uploadId);

    String uploadSingleFile(Long userId, MultipartFile file);
}