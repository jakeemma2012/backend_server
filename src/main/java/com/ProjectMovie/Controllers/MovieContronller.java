package com.ProjectMovie.Controllers;

import com.Exceptions.MovieException;
import com.ProjectMovie.Exceptions.EmptyFileException;
import com.ProjectMovie.Interface.MovieService;
import com.ProjectMovie.dto.MovieDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tsohr.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/movies")
@CrossOrigin(origins = "*")
public class MovieContronller {

    private final MovieService movieService;
    private final RestTemplate restTemplate;

    @Value("${base.url}")
    private String baseUrl;

    public MovieContronller(MovieService movieService) {
        this.movieService = movieService;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMoviesHandler() {
        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    }

    @GetMapping("/get/{movieId}")
    public ResponseEntity<MovieDTO> getMovieByIdHandler(@PathVariable int movieId) {
        return new ResponseEntity<>(movieService.getMovieById(movieId), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDTO> addMovieHandler(@RequestPart MultipartFile image,
                                                    @RequestPart MultipartFile video,
                                                    @RequestPart String movieDTO) throws IOException, EmptyFileException {
        // Validate files
        if (image == null || image.isEmpty()) {
            throw new EmptyFileException("Image file is required");
        }
        if (video == null || video.isEmpty()) {
            throw new EmptyFileException("Video file is required");
        }

        MovieDTO dto = convertToDTO(movieDTO);
        System.out.println("Movie data parsed successfully");

        // Validate release year
        if (dto.getReleaseYear() < 1900) {
            throw new IllegalArgumentException("Release year must be greater than or equal to 1900");
        }

        // Create multipart form data
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        ByteArrayResource fileResource = new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        };
        body.add("image", fileResource);

        fileResource = new ByteArrayResource(video.getBytes()) {
            @Override
            public String getFilename() {
                return video.getOriginalFilename();
            }
        };

        body.add("video", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String response = restTemplate.postForObject(
                baseUrl + "api/uploads_all",
                requestEntity,
                String.class
        );

        JSONObject rsp = new JSONObject(response);

        return new ResponseEntity<>(movieService.addMovie(dto, rsp.toString()), HttpStatus.CREATED);
    }


    private MovieDTO convertToDTO(String movietoObject) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movietoObject, MovieDTO.class);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable int movieId) throws IOException {
        return new ResponseEntity<>(movieService.deleteMovie(movieId), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDTO> updateMovieHandler(@PathVariable int movieId, @RequestPart MultipartFile file, @RequestPart String movieDTO) throws IOException {
        MovieDTO dto = convertToDTO(movieDTO);
        return new ResponseEntity<>(movieService.updateMovie(movieId, dto, file), HttpStatus.OK);
    }
}
