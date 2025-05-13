package com.ProjectMovie.Utils;

import com.ProjectMovie.dto.MovieDTO;

import lombok.Data;

@Data
public class CompleteUploadRequest {
    private MovieDTO movieDTO;
    private String imageName;
    private String videoName;
    private String backdropName;
    private String trailerName;
}
