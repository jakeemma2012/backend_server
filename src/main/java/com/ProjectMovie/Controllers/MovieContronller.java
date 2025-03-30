package com.ProjectMovie.Controllers;

import com.ProjectMovie.Exceptions.EmptyFileException;
import com.ProjectMovie.Interface.MovieService;
import com.ProjectMovie.dto.MovieDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@CrossOrigin(origins = "*")
public class MovieContronller {
    
    private final MovieService movieService;

    public MovieContronller(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDTO> addMovieHandler(@RequestPart MultipartFile file, @RequestPart String movieDTO) throws IOException, EmptyFileException {
        MovieDTO dto = convertToDTO(movieDTO);
        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }


    private MovieDTO convertToDTO(String movietoObject) throws  JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movietoObject, MovieDTO.class);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDTO>> getAllMoviesHandler(){
        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    }

    @GetMapping("/get/{movieId}")    
    public ResponseEntity<MovieDTO> getMovieByIdHandler(@PathVariable int movieId){
        return new ResponseEntity<>(movieService.getMovieById(movieId), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable int movieId) throws IOException{
        return new ResponseEntity<>(movieService.deleteMovie(movieId), HttpStatus.OK);
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDTO> updateMovieHandler(@PathVariable int movieId, @RequestPart MultipartFile file, @RequestPart String movieDTO) throws IOException{
        MovieDTO dto = convertToDTO(movieDTO);
        return new ResponseEntity<>(movieService.updateMovie(movieId, dto, file), HttpStatus.OK);
    }
    
    
}
