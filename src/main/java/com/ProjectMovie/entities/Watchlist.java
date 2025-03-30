package com.ProjectMovie.entities;

import com.ProjectMovie.Auth.Entities.User;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Watchlist {

    //Foreign key
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Generate value
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(nullable = false)
    @NotBlank(message = "Vui lòng nhập id phim !")
    private String movieId;
    
    public User getUser() {
        return user;
    }

    public String getMovieId() {
        return movieId;
    }

}
