package com.ProjectMovie.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Vui lòng nhập tiêu đề phim !")
    private String title;

    @Column(nullable = false)
    @NotNull(message = "Vui lòng nhập điểm đánh giá !")
    @Min(value = 0, message = "Điểm đánh giá không hợp lệ")
    @Max(value = 10, message = "Điểm đánh giá không hợp lệ")
    private Double rating;

    @Column(nullable = false, length = 2000)
    private String overviewString;

    @Column(nullable = false)
    @NotNull(message = "Vui lòng nhập thể loại phim !")
    private String genres;

    @Column(nullable = false)
    @NotNull(message = "Vui lòng nhập trạng thái phim !")
    @Enumerated(EnumType.STRING)
    private MovieStatus status;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập tên studio !")
    private String studio;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập tên đạo diễn !")
    private String director;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

    @Column(nullable = false)
    @NotNull(message = "Vui lòng nhập năm phát hành!")
    @Min(value = 1900, message = "Năm phát hành phải từ 1900 trở lên")
    @Max(value = 2100, message = "Năm phát hành không hợp lệ")
    private Integer releaseYear;

    @Column(nullable = false)
    @NotNull(message = "Vui lòng nhập thời lượng phim!")
    private Integer duration;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = true)
    private String videoUrl;

    @Column(nullable = true)
    private String backdropUrl;

    @Column(nullable = true)
    private String trailerUrl;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập quốc gia phim !")
    private String country;

}
