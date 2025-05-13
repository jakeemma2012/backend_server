package com.ProjectMovie.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.ProjectMovie.Services.ChunkUploadService;
import com.ProjectMovie.Utils.CompleteUploadRequest;
import com.ProjectMovie.dto.MovieDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/upload")
public class ChunkUploadController {

    @Autowired
    private ChunkUploadService chunkUploadService;

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${base.url.api}")
    private String cdnServerUrl;

    @PostMapping("/media")
    public ResponseEntity<Object> uploadMovie(
            @RequestPart("image") MultipartFile image,
            @RequestPart("video") MultipartFile video,
            @RequestPart("backdrop") MultipartFile backdrop,
            @RequestPart("movieDTO") String movieDTOJson) throws IOException {

        try {
            System.out.println("HAS PING CHUNK UPLOAD");

            chunkUploadService.uploadAllChunks(image, "image");
            chunkUploadService.uploadAllChunks(video, "video");
            chunkUploadService.uploadAllChunks(backdrop, "backdrop");

            System.out.println("HAS UPLOAD ALL CHUNKS");

            ObjectMapper mapper = new ObjectMapper();
            MovieDTO movieDTO = mapper.readValue(movieDTOJson, MovieDTO.class);

            System.out.println("HAS PARSE MOVIE DTO");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            CompleteUploadRequest completeRequest = new CompleteUploadRequest();
            completeRequest.setMovieDTO(movieDTO);
            completeRequest.setImageName(image.getOriginalFilename());
            completeRequest.setVideoName(video.getOriginalFilename());
            completeRequest.setBackdropName(backdrop.getOriginalFilename());

            HttpEntity<CompleteUploadRequest> requestEntity = new HttpEntity<>(completeRequest, headers);

            System.out.println("COMPLETE REQUEST: " + completeRequest);
            System.out.println("REQUEST ENTITY: " + requestEntity);

            System.out.println("PRE COMPLETE UPLOAD");
            ResponseEntity<Object> response = restTemplate.postForEntity(
                    cdnServerUrl + "/api/completeUpload?name=" + movieDTO.getTitle(),
                    requestEntity,
                    Object.class);

            System.out.println("RESPONSE COMPLETE UPLOAD: " + response);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}