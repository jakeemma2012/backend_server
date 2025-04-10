package com.ProjectMovie.Interface;

import com.ProjectMovie.Exceptions.EmptyFileException;
import com.ProjectMovie.dto.MovieDTO;
import org.apache.coyote.Response;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MovieService {

    MovieDTO addMovie(MovieDTO movieDTO, String rsp) throws IOException, EmptyFileException;
    
    MovieDTO getMovieById(int id);

    Map<String, Object> getAllMovies();

    MovieDTO updateMovie(int movieId, MovieDTO movieDTO, MultipartFile file) throws IOException;

    String deleteMovie(int movieId) throws IOException;
}
