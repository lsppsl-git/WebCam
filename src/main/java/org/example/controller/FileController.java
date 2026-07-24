package org.example.controller;

import org.example.common.Result;
import org.example.dto.file.FileInitDTO;
import org.example.dto.file.FileInitResponseDTO;
import org.example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/init")
    public Result<FileInitResponseDTO> initUpload(
            @RequestParam Long userId,
            @RequestBody FileInitDTO dto) {
        FileInitResponseDTO response = fileService.initUpload(userId, dto);
        return Result.success(response);
    }

    @PostMapping("/chunk")
    public Result<Void> uploadChunk(
            @RequestParam String uploadId,
            @RequestParam Integer chunkIndex,
            @RequestParam MultipartFile file) {
        fileService.uploadChunk(uploadId, chunkIndex, file);
        return Result.success();
    }

    @PostMapping("/merge")
    public Result<Map<String, String>> mergeChunks(@RequestParam String uploadId) {
        Map<String, String> result = fileService.mergeChunks(uploadId);
        return Result.success(result);
    }

    @PostMapping("/upload")
    public Result<Map<String, String>> uploadSingleFile(
            @RequestParam Long userId,
            @RequestParam MultipartFile file) {
        String fileUrl = fileService.uploadSingleFile(userId, file);
        Map<String, String> result = new HashMap<>();
        result.put("fileUrl", fileUrl);
        result.put("fileName", file.getOriginalFilename());
        return Result.success(result);
    }
}