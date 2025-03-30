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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);
    private final MovieRepository movieRepository;
    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDTO addMovie(MovieDTO movieDTO, MultipartFile file) throws IOException {
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistException("Phim đã tồn tại, vui lòng upload file khác !");
        }
        logger.info("Starting addMovie process");

        // 1. upload the file
        logger.debug("Uploading file");
        String fileName = fileService.uploadFile(path, file);
        logger.debug("File uploaded successfully: {}", fileName);

        // 2. set value poster as filename
        movieDTO.setPoster(fileName);
        logger.debug("Poster filename set: {}", fileName);

        // Generate poster URL
        String posterUrl = baseUrl + "file/" + fileName;
        movieDTO.setPosterUrl(posterUrl);
        // 3. map dto to Movie object
        logger.debug("Mapping DTO to Movie entity");

        // Movie movie = new Movie(
        //         null, // movieId should be null for new entities
        //         movieDTO.getTitle(),
        //         movieDTO.getDirector(),
        //         movieDTO.getStudio(),
        //         movieDTO.getMovieCast(),
        //         movieDTO.getReleaseYear(),
        //         movieDTO.getPoster(),
        //         posterUrl);

        Movie movie = null;
        
        // 4. save -> saved
        logger.debug("Saving movie to database");
        Movie savedMovie = movieRepository.save(movie);

        // 5. map Movie object to MovieDTO and return
        MovieDTO response = new MovieDTO(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl);

        logger.info("Completed addMovie process successfully");
        return response;
    }

    @Override
    public MovieDTO getMovieById(int id) {
        // 1. check DB có không
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException("Không tìm thấy phim id : " + id));
        // 2. nếu có thì trả về
        String posterUrl = baseUrl + "file/" + movie.getPoster();
        // 3. nếu không thì trả về null

        MovieDTO movieDTO = new MovieDTO(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl);
        // 4. nếu có thì trả về movieDTO

        return movieDTO;
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        // 1. fetch hết movie trong DB

        List<Movie> movies = movieRepository.findAll();

        List<MovieDTO> movieDTOs = new ArrayList<>();

        for (Movie movie : movies) {
            String posterUrl = baseUrl + "file/" + movie.getPoster();
            MovieDTO movieDTO = new MovieDTO(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl);
            movieDTOs.add(movieDTO);
        }
        return movieDTOs;
    }

    @Override
    public MovieDTO updateMovie(int movieId, MovieDTO movieDTO, MultipartFile file) throws IOException {
        // 1. check DB có không
        // 2. nếu có thì update
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Không tìm thấy phim id : " + movieId));

        String fileName = movie.getPoster();
        if (file != null) {
            Files.delete(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        String posterUrl = baseUrl + "file/" + fileName;

        movieDTO.setPoster(fileName);

        // Movie movie2 = new Movie(
        //         movie.getMovieId(),
        //         movieDTO.getTitle(),
        //         movieDTO.getDirector(),
        //         movieDTO.getStudio(),
        //         movieDTO.getMovieCast(),
        //         movieDTO.getReleaseYear(),
        //         movieDTO.getPoster(),
        //         posterUrl);

        Movie movie2 = null;

        Movie updatedMovie = movieRepository.save(movie2);

        MovieDTO response = new MovieDTO(
                updatedMovie.getMovieId(),
                updatedMovie.getTitle(),
                updatedMovie.getDirector(),
                updatedMovie.getStudio(),
                updatedMovie.getMovieCast(),
                updatedMovie.getReleaseYear(),
                updatedMovie.getPoster(),
                posterUrl);
                

        return response;
    }

    @Override
    public String deleteMovie(int movieId) throws IOException {
        // 1. check DB có không
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Không tìm thấy phim id : " + movieId));
        int id = movie.getMovieId();
        
        Files.delete(Paths.get(path + File.separator + movie.getPoster()));
        // 2. nếu có thì delete
        movieRepository.deleteById(movieId);
        // 3. nếu không thì trả về null
        return "Movie " + id + " deleted successfully";
    }

}