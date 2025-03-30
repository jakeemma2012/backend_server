package com.ProjectMovie.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MovieDTO {
    
    private Integer movieId;

    @NotBlank(message = "Vui lòng nhập tiêu đề phim !")
    private String title;
    
    @NotBlank(message = "Vui lòng nhập tên đạo diễn !")
    private String director;

    @NotBlank(message = "Vui lòng nhập tên studio !")
    private String studio;

    private Set<String> movieCast;
    
    private Integer releaseYear;

    @NotBlank(message = "Vui lòng nhập poster !")
    private String poster;

    @NotBlank(message = "Vui lòng nhập đường dẫn poster !")
    private String posterUrl;

}
