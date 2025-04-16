package com.ProjectMovie.Controllers;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/v1/assets")
@CrossOrigin(origins = "*")
public class AssetsController {

    private final RestTemplate restTemplate;

    public AssetsController() {
        this.restTemplate = new RestTemplate();
    }

    @Value("${base.url.api}")
    private String baseUrlApi;

    @GetMapping()
    public ResponseEntity<?> getAsset(@RequestParam("linkAssets") String linkAssets) {
        try {

            System.out.println(linkAssets);

            List<String> videoExtensions = Arrays.asList(".mp4", ".avi", ".mkv", ".mov", ".wmv", ".flv", ".webm",
                    ".m4v", ".3gp", ".ogv");
            // List<String> imageExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif",
            // ".bmp", ".tiff", ".svg", ".webp", ".heic", ".ico");

            boolean isVideo = videoExtensions.stream().anyMatch(linkAssets::endsWith);
            // boolean isImage = imageExtensions.stream().anyMatch(linkAssets::endsWith);

            String url;
            if (isVideo) {
                url = baseUrlApi + "/api/get_assets?linkVideo=" + linkAssets;
            } else {
                url = baseUrlApi + "/api/get_assets?linkImage=" + linkAssets;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(url));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }
}
