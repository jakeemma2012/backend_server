package com.ProjectMovie.Interface;

import com.ProjectMovie.Exceptions.EmptyFileException;
import com.ProjectMovie.dto.MovieDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

public interface MovieService {

    MovieDTO addMovie(MovieDTO movieDTO, String response)
            throws IOException, EmptyFileException;

    MovieDTO getMovieById(int id);

    List<MovieDTO> getAllMovies();

    MovieDTO updateMovie(int movieId, MovieDTO movieDTO, MultipartFile file) throws IOException;

    String deleteMovie(int movieId) throws IOException;
}
