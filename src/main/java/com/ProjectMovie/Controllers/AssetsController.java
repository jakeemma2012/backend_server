package com.ProjectMovie.Controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.StreamUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.ProjectMovie.Auth.Repositories.MovieRepositories;
import com.ProjectMovie.entities.Movie;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/v1/assets")
@CrossOrigin(origins = "*")
public class AssetsController {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${base.url.api}")
    private String baseUrlApi;

    @Value("${base.url}")
    private String baseUrl;

    @Value("${base.url.apiLocal}")
    private String baseUrlApiLocal;

    // @GetMapping("/get_assets")
    // public ResponseEntity<?> getAsset(@RequestParam("linkAssets") String
    // linkAssets,
    // @RequestParam("nameTag") String nameTag) {
    // try {
    // if (linkAssets == null || linkAssets.trim().isEmpty() || nameTag == null ||
    // nameTag.trim().isEmpty()) {
    // return ResponseEntity.badRequest().body("Link assets and nameTag cannot be
    // empty");
    // }

    // System.out.println("linkAssets: " + linkAssets);
    // System.out.println("nameTag: " + nameTag);

    // String url = "";
    // switch (nameTag) {
    // case "poster":
    // url = baseUrlApi + "/api/get_assets?linkImage="
    // + URLEncoder.encode(linkAssets, StandardCharsets.UTF_8) + "&nameTag=" +
    // nameTag;
    // break;
    // case "backdrop":
    // url = baseUrlApi + "/api/get_assets?linkBackdrop="
    // + URLEncoder.encode(linkAssets, StandardCharsets.UTF_8) + "&nameTag=" +
    // nameTag;
    // break;
    // case "video":
    // url = baseUrlApi + "/api/get_assets?linkVideo="
    // + URLEncoder.encode(linkAssets, StandardCharsets.UTF_8) + "&nameTag=" +
    // nameTag;
    // break;
    // case "cast":
    // url = baseUrlApi + "/api/get_assets?linkCast="
    // + URLEncoder.encode(linkAssets, StandardCharsets.UTF_8) + "&nameTag=" +
    // nameTag;
    // break;
    // default:
    // return ResponseEntity.badRequest().body("Invalid nameTag: " + nameTag);
    // }

    // System.out.println("Redirecting to: " + url);

    // HttpHeaders headers = new HttpHeaders();
    // headers.setLocation(URI.create(url));
    // return new ResponseEntity<>(headers, HttpStatus.FOUND);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body("Error processing request: " + e.getMessage());
    // }
    // }

    @GetMapping("/get_assets")
    public ResponseEntity<?> getAssets(@RequestParam("linkAssets") String linkAssets,
            @RequestParam("nameTag") String nameTag) {
        try {
            System.out.println("nameTag: " + nameTag);
            if (linkAssets == null || linkAssets.trim().isEmpty() || nameTag == null ||
                    nameTag.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Link assets & nameTag cannot be empty");
            }

            System.out.println("Original linkAssets: " + linkAssets);

            List<String> videoExtensions = Arrays.asList(".mp4", ".avi", ".mkv", ".mov",
                    ".wmv", ".flv", ".webm",
                    ".m4v", ".3gp", ".ogv");

            boolean isVideo = videoExtensions.stream().anyMatch(linkAssets::endsWith);

            String url = "";
            switch (nameTag) {
                case "poster":
                    url = baseUrlApi + "/api/get_assets?linkImage="
                            + URLEncoder.encode(linkAssets, StandardCharsets.UTF_8) + "&nameTag=" + nameTag;
                    break;
                case "backdrop":
                    url = baseUrlApi + "/api/get_assets?linkBackdrop="
                            + URLEncoder.encode(linkAssets, StandardCharsets.UTF_8) + "&nameTag=" + nameTag;
                    break;
                case "video":
                    linkAssets = linkAssets.replaceAll("%2F", "/");
                    System.out.println("link : " + linkAssets);
                    url = baseUrlApi + "/" + linkAssets;
                    break;
                case "cast":
                    url = baseUrlApi + "/api/get_assets?linkCast="
                            + URLEncoder.encode(linkAssets, StandardCharsets.UTF_8) + "&nameTag=" + nameTag;
                    break;
                case "trailer":
                    linkAssets = linkAssets.replaceAll("%2F", "/");
                    url = baseUrlApi + "/" + linkAssets + "/master.m3u8";
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid nameTag: " + nameTag);
            }

            System.out.println("Returning URL: " + url);

            Map<String, String> response = new HashMap<>();
            response.put("url", url);
            response.put("type", isVideo ? "video" : "image");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/get_assets_web")
    public void getAssetsWeb(@RequestParam("linkAssets") String linkAssets,
            @RequestParam("nameTag") String nameTag,
            @RequestParam(value = "nameMovie", required = false) String nameMovie,
            HttpServletResponse response) {
        try {
            if (linkAssets == null || linkAssets.trim().isEmpty() || nameTag == null ||
                    nameTag.trim().isEmpty()) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write("Link assets & nameTag cannot be empty");
                return;
            }

            String url = "";
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

            switch (nameTag) {
                case "poster":
                case "backdrop":
                case "cast":
                    mediaType = MediaType.IMAGE_JPEG;
                    break;
                case "video":
                case "trailer":
                    mediaType = MediaType.valueOf("video/mp4");
                    break;
                default:
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    response.getWriter().write("Invalid nameTag: " + nameTag);
                    return;
            }

            switch (nameTag) {
                case "poster":
                    url = baseUrlApi + "/api/get_assets?linkImage=" + linkAssets + "&nameTag=" + nameTag;
                    break;
                case "backdrop":
                    url = baseUrlApi + "/api/get_assets?linkBackdrop=" + linkAssets + "&nameTag=" + nameTag;
                    break;
                case "video":
                    linkAssets = linkAssets.replaceAll("%2F", "/");
                    url = baseUrlApi + "/" + linkAssets;
                    break;
                case "cast":
                    if (nameMovie != null) {
                        url = baseUrlApi + "/api/get_assets?linkCast="
                                + linkAssets + "&nameMovie=" + nameMovie + "&nameTag=" + nameTag;
                    } else {
                    }
                    break;
                case "trailer":
                    linkAssets = linkAssets.replaceAll("%2F", "/");
                    url = baseUrlApi + "/" + linkAssets + "/master.m3u8";
                    break;
            }

            System.out.println("Redirecting to URL: " + url);

            response.setContentType(mediaType.toString());

            restTemplate.execute(url, HttpMethod.GET, null, clientHttpResponse -> {
                clientHttpResponse.getHeaders().forEach((name, values) -> {
                    values.forEach(value -> {
                        if (!name.equalsIgnoreCase("Content-Length") && !name.equalsIgnoreCase("Transfer-Encoding")) {
                            response.setHeader(name, value);
                        }
                    });
                });

                StreamUtils.copy(clientHttpResponse.getBody(), response.getOutputStream());
                return null;
            });

        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write("Error processing request: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @GetMapping("/get_trailer")
    public ResponseEntity<?> getTrailer(@RequestParam("linkAssets") String linkAssets,
            @RequestParam("nameTag") String nameTag) {
        try {

            System.out.println("nameTag: " + nameTag);

            if (linkAssets == null || linkAssets.trim().isEmpty() || nameTag == null ||
                    nameTag.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Link assets & nameTag cannot be empty");
            }

            System.out.println("Original linkAssets: " + linkAssets);

            List<String> videoExtensions = Arrays.asList(".mp4", ".avi", ".mkv", ".mov",
                    ".wmv", ".flv", ".webm",
                    ".m4v", ".3gp", ".ogv");

            boolean isVideo = videoExtensions.stream().anyMatch(linkAssets::endsWith);

            String url = "";
            switch (nameTag) {
                case "video":
                    url = baseUrlApi + "/api/assets/get_trailer?linkAssets="
                            + URLEncoder.encode(linkAssets, StandardCharsets.UTF_8) + "&nameTag=video";
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid nameTag: " + nameTag);
            }

            System.out.println("Returning URL: " + url);

            Map<String, String> response = new HashMap<>();
            response.put("url", url);
            response.put("type", isVideo ? "video" : "image");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/get_cast_list")
    public ResponseEntity<?> getCastList(@RequestParam("nameMovie") String nameMovie) {
        try {
            System.out.println("Name Cast List: " + nameMovie);
            if (nameMovie == null || nameMovie.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Name movie cannot be empty");
            }

            String url = baseUrlApiLocal + "/api/get_cast_list?nameMovie=" + nameMovie;

            System.out.println("Returning CAST LIST URL: " + url);

            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String responseBody = responseEntity.getBody();
                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(responseEntity.getStatusCode())
                        .body("Error: " + responseEntity.getStatusCodeValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/get_cast")
    public ResponseEntity<?> getCast(@RequestParam("linkCast") String linkCast,
            @RequestParam("nameMovie") String nameMovie) {
        try {
            System.out.println("linkCast: " + linkCast);
            if (linkCast == null || linkCast.trim().isEmpty() || nameMovie == null ||
                    nameMovie.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Link cast & nameMovie cannot be empty");
            }

            System.out.println("Original linkCast: " + linkCast);

            linkCast = linkCast.replaceAll(" ", "");
            nameMovie = nameMovie.replaceAll(" ", "_");

            System.out.println("linkCast: " + linkCast);
            System.out.println("nameMovie: " + nameMovie);

            String url = "";
            url = baseUrlApi + "/api/get_assets?linkCast="
                    + URLEncoder.encode(linkCast, StandardCharsets.UTF_8) + "&nameMovie=" + nameMovie + "&nameTag=cast";

            System.out.println("Returning URL: " + url);

            Map<String, String> response = new HashMap<>();
            response.put("url", url);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/get_cast_web")
    public ResponseEntity<?> getCastWeb(@RequestParam("linkCast") String linkCast,
            @RequestParam("nameMovie") String nameMovie) {
        try {
            System.out.println("linkCast: " + linkCast);
            if (linkCast == null || linkCast.trim().isEmpty() || nameMovie == null ||
                    nameMovie.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Link cast & nameMovie cannot be empty");
            }

            System.out.println("Original linkCast: " + linkCast);

            linkCast = linkCast.replaceAll(" ", "");
            nameMovie = nameMovie.replaceAll(" ", "_");

            System.out.println("linkCast: " + linkCast);
            System.out.println("nameMovie: " + nameMovie);

            String url = "";
            url = baseUrlApi + "/api/get_assets?linkCast="
                    + URLEncoder.encode(linkCast, StandardCharsets.UTF_8) + "&nameMovie=" + nameMovie + "&nameTag=cast";

            System.out.println("Returning URL: " + url);

            Map<String, String> response = new HashMap<>();
            response.put("url", url);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

}
