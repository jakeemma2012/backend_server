package com.ProjectMovie.Controllers;

import com.ProjectMovie.Auth.Entities.User;
import com.ProjectMovie.Auth.Repositories.UserRepositories;
import com.ProjectMovie.Auth.Repositories.WatchListRepositories;
import com.ProjectMovie.Exceptions.EmptyFileException;
import com.ProjectMovie.Interface.MovieService;
import com.ProjectMovie.dto.MovieDTO;
import com.ProjectMovie.entities.Watchlist;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/movies")
@CrossOrigin(origins = "*")
public class MovieContronller {

    private final MovieService movieService;
    private final RestTemplate restTemplate;
    private final UserRepositories userRepository;
    private final WatchListRepositories watchListRepositories;

    @Value("${base.url.api}")
    private String baseUrlApi;

    public MovieContronller(MovieService movieService, UserRepositories userRepository,
            WatchListRepositories watchListRepositories) {
        this.movieService = movieService;
        this.restTemplate = new RestTemplate();
        this.userRepository = userRepository;
        this.watchListRepositories = watchListRepositories;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDTO> addMovieHandler(
            @RequestPart String movieDTO)
            throws IOException, EmptyFileException {
        MovieDTO dto = convertToDTO(movieDTO);
        return new ResponseEntity<>(movieService.addMovie(dto), HttpStatus.CREATED);
    }

    private MovieDTO convertToDTO(String movietoObject) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movietoObject, MovieDTO.class);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDTO>> getAllMoviesHandler() {
        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    }

    @GetMapping("/get/{movieId}")
    public ResponseEntity<MovieDTO> getMovieByIdHandler(@PathVariable int movieId) {
        return new ResponseEntity<>(movieService.getMovieById(movieId), HttpStatus.OK);
    }

    @GetMapping("/get_favorite")
    public ResponseEntity<List<MovieDTO>> getFavoriteHandler(@RequestParam("email") String email) {
        try {
            return new ResponseEntity<>(movieService.getFavorite(email), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<Map<String, Object>> deleteMovieHandler(@PathVariable int movieId) throws IOException {
        return new ResponseEntity<>(movieService.deleteMovie(movieId), HttpStatus.OK);
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDTO> updateMovieHandler(@PathVariable int movieId, @RequestPart String movieDTO)
            throws IOException {
        MovieDTO dto = convertToDTO(movieDTO);
        return new ResponseEntity<>(movieService.updateMovie(movieId, dto), HttpStatus.OK);
    }

    @PostMapping("/add_favorite")
    public ResponseEntity<String> addFavoriteHandler(@RequestParam("email") String email,
            @RequestParam("movieId") int movieId) {
        try {

            User user = userRepository.findByEmail(email).orElseThrow();

            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            Watchlist watchlist = watchListRepositories.findByUserAndMovieId(user, movieId);

            if (watchlist != null) {
                return new ResponseEntity<>("Already in favorite", HttpStatus.CONFLICT);
            }

            Watchlist watchlist_create = new Watchlist(
                    null,
                    user,
                    movieId);

            watchListRepositories.save(watchlist_create);

            return new ResponseEntity<>("Added to favorite", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error adding to favorite", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete_favorite")
    public ResponseEntity<String> deleteFavoriteHandler(@RequestParam("email") String email,
            @RequestParam("movieId") int movieId) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        Watchlist watchlist = watchListRepositories.findByUserAndMovieId(user, movieId);

        if (watchlist == null) {
            return new ResponseEntity<>("Watchlist not found", HttpStatus.NOT_FOUND);
        }

        watchListRepositories.delete(watchlist);

        return new ResponseEntity<>("Deleted from favorite", HttpStatus.OK);
    }

}
