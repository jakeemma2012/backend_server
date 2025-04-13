package com.ProjectMovie.Services;

import com.ProjectMovie.Exceptions.FileExistException;
import com.ProjectMovie.Exceptions.MovieNotFoundException;
import com.ProjectMovie.Interface.FileService;
import com.ProjectMovie.Interface.MovieService;
import com.ProjectMovie.dto.MovieDTO;
import com.ProjectMovie.entities.Movie;
import com.ProjectMovie.repositories.MovieRepository;
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

import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

    @Value("${base.url.api}")
    private String baseUrlApi;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public MovieDTO addMovie(MovieDTO movieDTO, String response) throws IOException {
        logger.info("Starting addMovie process");

        JSONObject jsonObject = new JSONObject(response);
        String imageUrl = jsonObject.getJSONObject("data").getJSONObject("linkIMG").getString("link");
        String videoUrl = jsonObject.getJSONObject("data").getJSONObject("linkVIDEO").getString("link");
        // 1. upload the file
        movieDTO.setImageUrl(imageUrl);
        movieDTO.setVideoUrl(videoUrl);
        // 3. map dto to Movie object
        logger.debug("Mapping DTO to Movie entity");

        Movie movie = new Movie(
                null, // movieId should be null for new entities
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
                imageUrl,
                videoUrl);

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
                savedMovie.getVideoUrl());

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
                movie.getVideoUrl());
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
                    movie.getVideoUrl());
            movieDTOs.add(movieDTO);
        }
        return movieDTOs;
    }

    @Override
    public MovieDTO updateMovie(int movieId, MovieDTO movieDTO, MultipartFile file) throws IOException {
        // // 1. check DB có không
        // // 2. nếu có thì update
        // Movie movie = movieRepository.findById(movieId)
        // .orElseThrow(() -> new MovieNotFoundException("Không tìm thấy phim id : " +
        // movieId));

        // String fileName = movie.getImageUrl();
        // if (file != null) {
        // Files.delete(Paths.get(path + File.separator + fileName));
        // fileName = fileService.uploadFile(path, file);
        // }

        // String posterUrl = baseUrl + "file/" + fileName;

        // movieDTO.setPoster(fileName);

        // // Movie movie2 = new Movie(
        // // movie.getMovieId(),
        // // movieDTO.getTitle(),
        // // movieDTO.getDirector(),
        // // movieDTO.getStudio(),
        // // movieDTO.getMovieCast(),
        // // movieDTO.getReleaseYear(),
        // // movieDTO.getPoster(),
        // // posterUrl);

        // Movie movie2 = null;

        // Movie updatedMovie = movieRepository.save(movie2);

        // MovieDTO response = new MovieDTO(
        // updatedMovie.getMovieId(),
        // updatedMovie.getTitle(),
        // updatedMovie.getRating(),
        // updatedMovie.getOverviewString(),
        // updatedMovie.getGenres(),
        // updatedMovie.getStatus(),
        // updatedMovie.getStudio(),
        // updatedMovie.getDirector(),
        // updatedMovie.getPoster(),
        // updatedMovie.getMovieCast(),
        // posterUrl,
        // updatedMovie.getReleaseYear());

        // return response;
        return null;
    }

    @Override
    public String deleteMovie(int movieId) throws IOException {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Không tìm thấy phim id : " + movieId));
        int id = movie.getMovieId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String[]> requestBody = new HashMap<>();
        requestBody.put("linkImage", new String[] { movie.getImageUrl() });
        requestBody.put("linkVideo", new String[] { movie.getVideoUrl() });

        HttpEntity<Map<String, String[]>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> rsp = restTemplate.postForEntity(
                baseUrlApi + "/api/delete_file",
                requestEntity,
                String.class);

        movieRepository.deleteById(movieId);

        return "Delete response: " + rsp.getBody();
    }

}