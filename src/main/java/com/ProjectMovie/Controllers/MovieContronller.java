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
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@CrossOrigin(origins = "*")
public class MovieContronller {
    
    private final MovieService movieService;
    private final RestTemplate restTemplate;

    @Value("${external.server.url}")
    private String externalServerUrl = "http://localhost:3000";

    public MovieContronller(MovieService movieService) {
        this.movieService = movieService;
        this.restTemplate = new RestTemplate();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDTO> addMovieHandler(@RequestPart MultipartFile file, @RequestPart String movieDTO) throws IOException, EmptyFileException {
        MovieDTO dto = convertToDTO(movieDTO);
        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/forward-movie")
    public ResponseEntity<String> forwardMovieHandler(@RequestPart MultipartFile file, @RequestPart String movieDTO) throws IOException, JsonProcessingException {
        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        // Create multipart request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        // Add the file
        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("video", fileResource);
        
        // Add the DTO as JSON string
        body.add("movieDTO", movieDTO);
        
        // Build the request
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        // Forward the data to external server with correct endpoint
        String response = restTemplate.postForObject(
            "http://localhost:3000/api/uploads_video",
            requestEntity,
            String.class
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
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
