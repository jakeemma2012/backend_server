package com.ProjectMovie.Controllers;

import com.ProjectMovie.Exceptions.EmptyFileException;
import com.ProjectMovie.Interface.MovieService;
import com.ProjectMovie.dto.MovieDTO;
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

@RestController
@RequestMapping("/api/v1/movies")
@CrossOrigin(origins = "*")
public class MovieContronller {

    private final MovieService movieService;
    private final RestTemplate restTemplate;

    @Value("${base.url.api}")
    private String baseUrlApi;

    public MovieContronller(MovieService movieService) {
        this.movieService = movieService;
        this.restTemplate = new RestTemplate();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDTO> addMovieHandler(
            @RequestPart MultipartFile image,
            @RequestPart MultipartFile video,
            @RequestPart String movieDTO)
            throws IOException, EmptyFileException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("image", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });

        body.add("video", new ByteArrayResource(video.getBytes()) {
            @Override
            public String getFilename() {
                return video.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        System.out.println(baseUrlApi + "/api/uploads_all");

        String respString = restTemplate.postForObject(
                baseUrlApi + "/api/uploads_all",
                requestEntity,
                String.class);

        System.out.println(respString);

        MovieDTO dto = convertToDTO(movieDTO);

        return new ResponseEntity<>(movieService.addMovie(dto, respString), HttpStatus.CREATED);
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

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable int movieId) throws IOException {
        return new ResponseEntity<>(movieService.deleteMovie(movieId), HttpStatus.OK);
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDTO> updateMovieHandler(@PathVariable int movieId, @RequestPart MultipartFile file,
            @RequestPart String movieDTO) throws IOException {
        MovieDTO dto = convertToDTO(movieDTO);
        return new ResponseEntity<>(movieService.updateMovie(movieId, dto, file), HttpStatus.OK);
    }

}
