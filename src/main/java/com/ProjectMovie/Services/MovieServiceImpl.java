package com.ProjectMovie.Services;

import com.ProjectMovie.Auth.Entities.User;
import com.ProjectMovie.Auth.Repositories.UserRepositories;
import com.ProjectMovie.Auth.Repositories.WatchListRepositories;
import com.ProjectMovie.Exceptions.MovieNotFoundException;
import com.ProjectMovie.Interface.FileService;
import com.ProjectMovie.Interface.MovieService;
import com.ProjectMovie.dto.MovieDTO;
import com.ProjectMovie.entities.Movie;
import com.ProjectMovie.entities.MovieStatus;
import com.ProjectMovie.entities.Watchlist;
import com.ProjectMovie.repositories.MovieRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

@Service
public class MovieServiceImpl implements MovieService {
        ObjectMapper objectMapper = new ObjectMapper();
        private static final Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);
        private final MovieRepository movieRepository;
        private final FileService fileService;
        private final RestTemplate restTemplate;

        @Value("${project.poster}")
        private String path;

        @Value("${base.url}")
        private String baseUrl;

        @Value("${base.url.api}")
        private String baseUrlApi;

        private final UserRepositories userRepository;
        private final WatchListRepositories watchListRepositories;

        public MovieServiceImpl(MovieRepository movieRepository, FileService fileService,
                        UserRepositories userRepository,
                        WatchListRepositories watchListRepositories) {
                this.movieRepository = movieRepository;
                this.fileService = fileService;
                this.restTemplate = new RestTemplate();
                this.userRepository = userRepository;
                this.watchListRepositories = watchListRepositories;
        }

        @Override
        public MovieDTO addMovie(MovieDTO movieDTO) throws IOException {
                if (movieDTO.getVideoUrl() == null) {
                        movieDTO.setStatus(MovieStatus.UPCOMING);
                }
                logger.info("Starting addMovie process");
                // 3. map dto to Movie object
                logger.debug("Mapping DTO to Movie entity");

                Movie movie = new Movie(
                                null,
                                movieDTO.getTitle(),
                                movieDTO.getRating(),
                                movieDTO.getOverviewString(),
                                movieDTO.getGenres(),
                                movieDTO.getStatus(),
                                movieDTO.getStudio(),
                                movieDTO.getDirector(),
                                movieDTO.getMovieCast(),
                                movieDTO.getReleaseYear(),
                                movieDTO.getDuration(),
                                movieDTO.getImageUrl(),
                                movieDTO.getVideoUrl(),
                                movieDTO.getBackdropUrl(),
                                movieDTO.getTrailerUrl(),
                                movieDTO.getCountry());

                // 4. save -> saved
                logger.debug("Saving movie to database");
                Movie savedMovie = movieRepository.save(movie);

                // 5. map Movie object to MovieDTO and return
                MovieDTO responseMovieDTO = new MovieDTO(
                                savedMovie.getMovieId(),
                                savedMovie.getTitle(),
                                savedMovie.getRating(),
                                savedMovie.getOverviewString(),
                                savedMovie.getGenres(),
                                savedMovie.getStatus(),
                                savedMovie.getStudio(),
                                savedMovie.getDirector(),
                                savedMovie.getMovieCast(),
                                savedMovie.getReleaseYear(),
                                savedMovie.getDuration(),
                                savedMovie.getImageUrl(),
                                savedMovie.getVideoUrl(),
                                savedMovie.getBackdropUrl(),
                                savedMovie.getTrailerUrl(),
                                savedMovie.getCountry());

                logger.info("Completed addMovie process successfully");

                return responseMovieDTO;
        }

        @Override
        public MovieDTO getMovieById(int id) {
                // 1. check DB có không
                Movie movie = movieRepository.findById(id)
                                .orElseThrow(() -> new MovieNotFoundException("Không tìm thấy phim id : " + id));
                // 2. nếu có thì trả về
                MovieDTO movieDTO = new MovieDTO(
                                movie.getMovieId(),
                                movie.getTitle(),
                                movie.getRating(),
                                movie.getOverviewString(),
                                movie.getGenres(),
                                movie.getStatus(),
                                movie.getStudio(),
                                movie.getDirector(),
                                movie.getMovieCast(),
                                movie.getReleaseYear(),
                                movie.getDuration(),
                                movie.getImageUrl(),
                                movie.getVideoUrl(),
                                movie.getBackdropUrl(),
                                movie.getTrailerUrl(),
                                movie.getCountry());

                // 4. nếu có thì trả về movieDTO
                return movieDTO;
        }

        @Override
        public List<MovieDTO> getAllMovies() {
                // 1. fetch hết movie trong DB
                List<Movie> movies = movieRepository.findAll();

                List<MovieDTO> movieDTOs = new ArrayList<>();

                for (Movie movie : movies) {
                        MovieDTO movieDTO = new MovieDTO(
                                        movie.getMovieId(),
                                        movie.getTitle(),
                                        movie.getRating(),
                                        movie.getOverviewString(),
                                        movie.getGenres(),
                                        movie.getStatus(),
                                        movie.getStudio(),
                                        movie.getDirector(),
                                        movie.getMovieCast(),
                                        movie.getReleaseYear(),
                                        movie.getDuration(),
                                        movie.getImageUrl(),
                                        movie.getVideoUrl(),
                                        movie.getBackdropUrl(),
                                        movie.getTrailerUrl(),
                                        movie.getCountry());

                        movieDTOs.add(movieDTO);
                }
                return movieDTOs;
        }

        @Override
        public MovieDTO updateMovie(MovieDTO movieDTO) throws IOException {
                // 1. Kiểm tra tồn tại
                Movie movie = movieRepository.findById(movieDTO.getMovieId())
                                .orElseThrow(() -> new MovieNotFoundException(
                                                "Không tìm thấy phim id: " + movieDTO.getMovieId()));

                // 2. Cập nhật thông tin từ DTO vào entity

                movie.setTitle(movieDTO.getTitle());
                movie.setRating(movieDTO.getRating());
                movie.setOverviewString(movieDTO.getOverviewString());
                movie.setGenres(movieDTO.getGenres());
                movie.setStatus(movieDTO.getStatus());
                movie.setStudio(movieDTO.getStudio());
                movie.setDirector(movieDTO.getDirector());
                movie.setMovieCast(movieDTO.getMovieCast());
                movie.setReleaseYear(movieDTO.getReleaseYear());
                movie.setDuration(movieDTO.getDuration());
                movie.setImageUrl(movieDTO.getImageUrl());
                movie.setVideoUrl(movieDTO.getVideoUrl());
                movie.setBackdropUrl(movieDTO.getBackdropUrl());
                movie.setTrailerUrl(movieDTO.getTrailerUrl());

                // 3. Lưu lại vào DB
                Movie updatedMovie = movieRepository.save(movie);

                // 4. Trả về DTO
                return new MovieDTO(
                                updatedMovie.getMovieId(),
                                updatedMovie.getTitle(),
                                updatedMovie.getRating(),
                                updatedMovie.getOverviewString(),
                                updatedMovie.getGenres(),
                                updatedMovie.getStatus(),
                                updatedMovie.getStudio(),
                                updatedMovie.getDirector(),
                                updatedMovie.getMovieCast(),
                                updatedMovie.getReleaseYear(),
                                updatedMovie.getDuration(),
                                updatedMovie.getImageUrl(),
                                updatedMovie.getVideoUrl(),
                                updatedMovie.getBackdropUrl(),
                                updatedMovie.getTrailerUrl(),
                                updatedMovie.getCountry());
        }

        @Override
        public Map<String, Object> deleteMovie(int movieId) throws IOException {
                Movie movie = movieRepository.findById(movieId)
                                .orElseThrow(() -> new MovieNotFoundException("Không tìm thấy phim id : " + movieId));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, String[]> requestBody = new HashMap<>();
                requestBody.put("linkImage", new String[] { movie.getImageUrl() });
                requestBody.put("linkVideo", new String[] { movie.getVideoUrl() });
                requestBody.put("linkBackdrop", new String[] { movie.getBackdropUrl() });

                HttpEntity<Map<String, String[]>> requestEntity = new HttpEntity<>(requestBody, headers);

                ResponseEntity<String> rsp = restTemplate.postForEntity(
                                baseUrlApi + "/api/delete_file",
                                requestEntity,
                                String.class);

                movieRepository.deleteById(movieId);
                Map<String, Object> responseBody = objectMapper.readValue(rsp.getBody(), new TypeReference<>() {
                });

                return Map.of(
                                "status", "success",
                                "message", "Delete response",
                                "response", responseBody);
        }

        @Override
        public List<MovieDTO> getFavorite(String email) throws Exception {
                User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));

                List<Watchlist> watchlist = watchListRepositories.findByUser(user);
                List<MovieDTO> movieDTOs = new ArrayList<>();
                for (Watchlist watchlist1 : watchlist) {
                        MovieDTO movieDTO = getMovieById(watchlist1.getMovieId());
                        movieDTOs.add(movieDTO);
                }
                return movieDTOs;
        }

        @Override
        public boolean checkFavorite(String email, int movieId) throws Exception {
                User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
                Watchlist watchlist = watchListRepositories.findByUserAndMovieId(user, movieId);
                return watchlist != null;
        }
}