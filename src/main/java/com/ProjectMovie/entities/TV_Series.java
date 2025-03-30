package com.ProjectMovie.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TV_Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập id phim !")
    private Integer movie_id;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập tổng số tập phim !")
    @Min(value = 1, message = "Số tập phim không hợp lệ")
    private Integer total_seasons;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập tổng số tập phim !")
    private Integer total_episodes;
}
