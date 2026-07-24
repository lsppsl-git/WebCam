package org.example.service.impl;

import org.example.common.BusinessException;
import org.example.dto.file.FileInitDTO;
import org.example.dto.file.FileInitResponseDTO;
import org.example.entity.FileInfo;
import org.example.mapper.FileInfoMapper;
import org.example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.chunk-size}")
    private Integer chunkSize;

    @Override
    @Transactional
    public FileInitResponseDTO initUpload(Long userId, FileInitDTO dto) {
        FileInfo existingFile = fileInfoMapper.findByMd5(dto.getMd5());
        if (existingFile != null) {
            return FileInitResponseDTO.builder()
                    .uploadId(existingFile.getUploadId())
                    .exists(true)
                    .fileUrl("/files/" + existingFile.getFilePath())
                    .uploadedChunks(new ArrayList<>())
                    .build();
        }

        String uploadId = UUID.randomUUID().toString();

        FileInfo fileInfo = FileInfo.builder()
                .uploadId(uploadId)
                .fileName(dto.getFileName())
                .fileType(dto.getFileType())
                .fileSize(dto.getFileSize())
                .md5(dto.getMd5())
                .totalChunks(dto.getTotalChunks())
                .uploadedChunks(0)
                .status("UPLOADING")
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        fileInfoMapper.insert(fileInfo);

        File tempDir = new File(uploadPath + "temp/" + uploadId);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        return FileInitResponseDTO.builder()
                .uploadId(uploadId)
                .exists(false)
                .uploadedChunks(new ArrayList<>())
                .build();
    }

    @Override
    public void uploadChunk(String uploadId, Integer chunkIndex, MultipartFile file) {
        FileInfo fileInfo = fileInfoMapper.findByUploadId(uploadId);
        if (fileInfo == null) {
            throw BusinessException.of(404, "上传任务不存在");
        }

        if (fileInfo.getStatus().equals("COMPLETED")) {
            return;
        }

        try {
            File tempDir = new File(uploadPath + "temp/" + uploadId);
            File chunkFile = new File(tempDir, chunkIndex.toString());
            file.transferTo(chunkFile);

            fileInfo.setUploadedChunks(fileInfo.getUploadedChunks() + 1);
            fileInfo.setUpdatedAt(LocalDateTime.now());
            fileInfoMapper.updateById(fileInfo);
        } catch (IOException e) {
            throw BusinessException.of(500, "分片上传失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Map<String, String> mergeChunks(String uploadId) {
        FileInfo fileInfo = fileInfoMapper.findByUploadId(uploadId);
        if (fileInfo == null) {
            throw BusinessException.of(404, "上传任务不存在");
        }

        if (!fileInfo.getUploadedChunks().equals(fileInfo.getTotalChunks())) {
            throw BusinessException.of(400, "分片未全部上传完成");
        }

        try {
            String fileExtension = getFileExtension(fileInfo.getFileName());
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            String filePath = "files/" + newFileName;
            File finalFile = new File(uploadPath + filePath);

            try (RandomAccessFile raf = new RandomAccessFile(finalFile, "rw")) {
                for (int i = 0; i < fileInfo.getTotalChunks(); i++) {
                    File chunkFile = new File(uploadPath + "temp/" + uploadId + "/" + i);
                    if (!chunkFile.exists()) {
                        throw BusinessException.of(400, "分片" + i + "不存在");
                    }

                    byte[] chunkData = Files.readAllBytes(chunkFile.toPath());
                    raf.seek((long) i * chunkSize);
                    raf.write(chunkData);

                    chunkFile.delete();
                }
            }

            File tempDir = new File(uploadPath + "temp/" + uploadId);
            tempDir.delete();

            fileInfo.setFilePath(filePath);
            fileInfo.setStatus("COMPLETED");
            fileInfo.setUpdatedAt(LocalDateTime.now());
            fileInfoMapper.updateById(fileInfo);

            Map<String, String> result = new HashMap<>();
            result.put("fileUrl", "/files/" + filePath);
            result.put("fileName", fileInfo.getFileName());

            return result;
        } catch (IOException e) {
            throw BusinessException.of(500, "文件合并失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String uploadSingleFile(Long userId, MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            String filePath = "files/" + newFileName;

            File destFile = new File(uploadPath + filePath);
            file.transferTo(destFile);

            String md5 = calculateMD5(file.getBytes());

            FileInfo fileInfo = FileInfo.builder()
                    .uploadId(UUID.randomUUID().toString())
                    .fileName(originalFilename)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .md5(md5)
                    .filePath(filePath)
                    .totalChunks(1)
                    .uploadedChunks(1)
                    .status("COMPLETED")
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            fileInfoMapper.insert(fileInfo);

            return "/files/" + filePath;
        } catch (IOException e) {
            throw BusinessException.of(500, "文件上传失败: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String calculateMD5(byte[] data) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }
}