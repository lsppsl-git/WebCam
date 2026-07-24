package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("file_info")
public class FileInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String uploadId;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String md5;

    private String filePath;

    private Integer totalChunks;

    private Integer uploadedChunks;

    private String status;

    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}