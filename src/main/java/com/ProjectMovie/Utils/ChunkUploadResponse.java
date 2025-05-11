package com.ProjectMovie.Utils;

import lombok.Data;

@Data
public class ChunkUploadResponse {
    private boolean success;
    private String message;
    private double progress;
}
