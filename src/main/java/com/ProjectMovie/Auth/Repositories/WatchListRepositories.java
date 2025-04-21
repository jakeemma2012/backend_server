package com.ProjectMovie.Auth.Repositories;

import java.util.List;

import org.springframework.boot.autoconfigure.ssl.SslProperties.Bundles.Watch;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ProjectMovie.entities.Watchlist;
import com.ProjectMovie.Auth.Entities.User;

public interface WatchListRepositories extends JpaRepository<Watchlist, Integer> {

    Watchlist findByUserAndMovieId(User user, int movieId);

    List<Watchlist> findByUser(User user);

}
