package com.ProjectMovie.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Seasions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập id Tv Series phim !")
    private Integer tv_series_id;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập tên tập phim !")
    @Min(value = 1, message = "Số tập phim không hợp lệ")
    private Integer seasonNumber;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập tên tập phim !")
    @Min(value = 1, message = "Số tập phim không hợp lệ")
    private Integer total_episodes;

    @Column(nullable = false)
    @NotNull(message = "Vui lòng nhập ngày phát hành tập phim !")
    private Date release_date;

}
