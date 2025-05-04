package com.ProjectMovie.Interface;

import com.ProjectMovie.Exceptions.EmptyFileException;
import com.ProjectMovie.dto.MovieDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MovieService {

    MovieDTO addMovie(MovieDTO movieDTO, String response)
            throws IOException, EmptyFileException;

    MovieDTO getMovieById(int id);

    List<MovieDTO> getAllMovies();

    MovieDTO updateMovie(int movieID, MovieDTO movieDTO) throws IOException;

    Map<String, Object> deleteMovie(int movieId) throws IOException;

    List<MovieDTO> getFavorite(String email) throws Exception;
}
