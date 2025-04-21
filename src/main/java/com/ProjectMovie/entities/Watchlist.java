package com.ProjectMovie.entities;

import com.ProjectMovie.Auth.Entities.User;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "watchlist")
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // Foreign key
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @NotNull(message = "Vui lòng nhập id phim !")
    private int movieId;

    public User getUser() {
        return user;
    }

    public int getMovieId() {
        return movieId;
    }

}
