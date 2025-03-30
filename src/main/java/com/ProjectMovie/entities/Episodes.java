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
public class Episodes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập tên tập phim !")
    private Integer season_id;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập số tập phim !")
    @Min(value = 1, message = "Số tập phim không hợp lệ")
    private Integer episode_number;
    

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập tên tập phim !")
    private String title;

    @Column(nullable = false)
    @NotNull(message = "Vui lòng nhập thời lượng tập phim !")
    @Min(value = 0, message = "Thời lượng tập phim không hợp lệ")
    private int duration;


    @Column(nullable = false)
    @NotNull(message = "Vui lòng nhập ngày phát hành tập phim !")
    private Date release_date;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập poster url !")
    private String image_url;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập video url !")
    private String video_url;
}
