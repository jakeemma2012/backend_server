package com.ProjectMovie.Services;

import com.ProjectMovie.Exceptions.FileExistException;
import com.ProjectMovie.Exceptions.MovieNotFoundException;
import com.ProjectMovie.Interface.FileService;
import com.ProjectMovie.Interface.MovieService;
import com.ProjectMovie.dto.MovieDTO;
import com.ProjectMovie.entities.Movie;
import com.ProjectMovie.repositories.MovieRepository;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.tsohr.JSONObject;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MovieServiceImpl implements MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);
    private final MovieRepository movieRepository;
    private final FileService fileService;
    private final RestTemplate restTemplate;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public MovieDTO addMovie(MovieDTO movieDTO, String rsp) throws IOException {
        JSONObject rspJson = new JSONObject(rsp);

        String imageLink = rspJson.getJSONObject("data").getJSONObject("linkIMG").getString("link");
        String videoLink = rspJson.getJSONObject("data").getJSONObject("linkVIDEO").getString("link");

        // 2. set value poster as filename
        movieDTO.setImageUrl(imageLink);
        logger.debug("Poster filename set: {}", imageLink);

        movieDTO.setVideoUrl(videoLink);
        // 3. map dto to Movie object
        logger.debug("Mapping DTO to Movie entity");

        Movie movie = new Movie(
                null,
                movieDTO.getTitle(),
                movieDTO.getRating(),
                movieDTO.getOverview(),
                movieDTO.getGenres(),
                movieDTO.getStatus(),
                movieDTO.getStudio(),
                movieDTO.getDirector(),
                movieDTO.getMovieCast(),
                movieDTO.getReleaseYear(),
                movieDTO.getImageUrl(),
                videoLink);

        // 4. save -> saved
        logger.debug("Saving movie to database");
        Movie savedMovie = movieRepository.save(movie);

        // 5. map Movie object to MovieDTO and return
        MovieDTO response = new MovieDTO(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getRating(),
                savedMovie.getOverview(),
                savedMovie.getGenres(),
                savedMovie.getStatus(),
                savedMovie.getStudio(),
                savedMovie.getDirector(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getImageUrl(),
                videoLink);

        logger.info("Completed addMovie process successfully");
        return response;
    }

    @Override
    public MovieDTO getMovieById(int id) {
        // 1. check DB có không
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException("Không tìm thấy phim id : " + id));
        // 3. nếu không thì trả về null

        MovieDTO movieDTO = new MovieDTO(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getRating(),
                movie.getOverview(),
                movie.getGenres(),
                movie.getStatus(),
                movie.getStudio(),
                movie.getDirector(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getImageUrl(),
                movie.getVideoUrl());

        // 4. nếu có thì trả về movieDTO
        return movieDTO;
    }

    @Override
    public Map<String, Object> getAllMovies() {
        // 1. fetch hết movie trong DB
        List<Movie> movies = movieRepository.findAll();

        List<MovieDTO> movieDTOs = new ArrayList<>();

        for (Movie movie : movies) {
            MovieDTO movieDTO = new MovieDTO(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getRating(),
                    movie.getOverview(),
                    movie.getGenres(),
                    movie.getStatus(),
                    movie.getStudio(),
                    movie.getDirector(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getImageUrl(),
                    movie.getVideoUrl());
            movieDTOs.add(movieDTO);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("results", movieDTOs);
        
        return response;
    }

    @Override
    public MovieDTO updateMovie(int movieId, MovieDTO movieDTO, MultipartFile file) throws IOException {
//        // 1. check DB có không
//        // 2. nếu có thì update
//        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Không tìm thấy phim id : " + movieId));
//
//        String fileName = movie.getPoster();
//        if (file != null) {
//            Files.delete(Paths.get(path + File.separator + fileName));
//            fileName = fileService.uploadFile(path, file);
//        }
//
//        String posterUrl = baseUrl + "file/" + fileName;
//
//        movieDTO.setPoster(fileName);
//
//        // Movie movie2 = new Movie(
//        //         movie.getMovieId(),
//        //         movieDTO.getTitle(),
//        //         movieDTO.getDirector(),
//        //         movieDTO.getStudio(),
//        //         movieDTO.getMovieCast(),
//        //         movieDTO.getReleaseYear(),
//        //         movieDTO.getPoster(),
//        //         posterUrl);
//
//        Movie movie2 = null;
//
//        Movie updatedMovie = movieRepository.save(movie2);
//
//        MovieDTO response = new MovieDTO(
//                updatedMovie.getMovieId(),
//                updatedMovie.getTitle(),
//                updatedMovie.getDirector(),
//                updatedMovie.getStudio(),
//                updatedMovie.getMovieCast(),
//                updatedMovie.getReleaseYear(),
//                updatedMovie.getPoster(),
//                posterUrl);
//
//
//        return response;
        return null;
    }

    @Override
    public String deleteMovie(int movieId) throws IOException {
        // 1. check DB có không

        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Không tìm thấy phim id : " + movieId));

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("linkImage", movie.getImageUrl());

        body.add("linkVideo", movie.getVideoUrl());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "api/delete_file",
                requestEntity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println(response.getBody());
        }

        int id = movie.getMovieId();

        // 2. nếu có thì delete
        movieRepository.deleteById(movieId);
        // 3. nếu không thì trả về null
        return "Movie " + id + " deleted successfully";
    }

}