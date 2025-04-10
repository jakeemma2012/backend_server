package com.ProjectMovie.dto;

import com.ProjectMovie.entities.MovieStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Vui lòng nhập đánh giá phim")
    @Min(value = 0, message = "Điểm đánh giá không hợp lệ")
    private Double rating;

    @NotBlank(message = "Vui lòng nhập overView")
    private String overview;

    @NotBlank(message = "vui lòng nhập thể loại")
    private String genres;

    @NotNull(message = "Vui lòng nhập tình trạng phim")
    private MovieStatus status;

    @NotBlank(message = "Vui lòng nhập tên studio !")
    private String studio;

    @NotBlank(message = "Vui lòng nhập tên đạo diễn !")
    private String director;


    @NotBlank(message = "Vui lòng nhập tên các Diễn viên")
    private Set<String> movieCast;


    @NotNull(message = "Vui lòng nhập năm sản xuất")
    @Min(value = 1900, message = "Năm phát hành phải từ 1900 trở lên")
    private Integer releaseYear;

    @NotBlank(message = "Vui lòng nhập poster !")
    private String imageUrl;

    @NotBlank(message = "Vui lòng nhập đường dẫn poster !")
    private String videoUrl;
}
