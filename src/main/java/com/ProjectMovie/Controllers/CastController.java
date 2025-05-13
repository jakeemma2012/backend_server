package com.ProjectMovie.Controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/cast")
public class CastController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${base.url.api}")
    private String cdnServerUrl;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/uploadCast")
    public ResponseEntity<?> uploadCast(
            @RequestParam("movieName") String movieName,
            @RequestParam("castFiles") MultipartFile[] castFiles) {
        try {
            System.out.println("HAS PING UPLOAD CAST");

            System.out.println("movieName: " + movieName);
            System.out.println("castFiles: " + castFiles);

            // Tạo form data
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("movieName", movieName);

            // Thêm từng file cast với field name là "cast"
            for (MultipartFile file : castFiles) {
                body.add("cast", new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                });
            }

            // Gửi request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Gửi request và nhận response
            ResponseEntity<List<String>> response = restTemplate.exchange(
                    cdnServerUrl + "/api/uploadCast",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<String>>() {
                    });

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            System.out.println("Error uploading cast: " + e.getMessage());
            e.printStackTrace(); // In stack trace để debug
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload cast: " + e.getMessage());
        }
    }
}