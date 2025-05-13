package com.ProjectMovie.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import com.ProjectMovie.entities.MovieStatus;

import io.micrometer.common.lang.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MovieDTO {

    private Integer movieId;

    @NotBlank(message = "Vui lòng nhập tiêu đề phim !")
    private String title;

    private Double rating;

    private String overviewString;

    private String genres;

    private MovieStatus status;

    private String studio;

    private String director;

    private Set<String> movieCast;

    private Integer releaseYear;

    private Integer duration;

    private String imageUrl;

    private String videoUrl;

    private String backdropUrl;

    private String trailerUrl;
}
