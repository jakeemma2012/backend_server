package com.ProjectMovie.Auth.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ProjectMovie.entities.Movie;

public interface MovieRepositories extends JpaRepository<Movie, Integer> {

    Movie findByMovieId(int movieId);

}
