package com.ProjectMovie.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Actors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    
    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập tên diễn viên !")
    private String name;


    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập Profile url !")
    private String profileUrl;
    
}
