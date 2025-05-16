package com.ProjectMovie.repositories;

import com.ProjectMovie.entities.Movie;
import com.ProjectMovie.entities.MovieStatus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByStatus(MovieStatus status);
}
