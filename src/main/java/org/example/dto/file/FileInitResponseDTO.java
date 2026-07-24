package org.example.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInitResponseDTO {

    private String uploadId;

    private Boolean exists;

    private String fileUrl;

    private List<Integer> uploadedChunks;
}