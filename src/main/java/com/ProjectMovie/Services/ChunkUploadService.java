package com.ProjectMovie.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.web.multipart.MultipartFile;

import com.ProjectMovie.Utils.ChunkUploadResponse;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.concurrent.ExecutionException;

@Service
public class ChunkUploadService {
    @Value("${base.url.api}")
    private String cdnServerUrl;

    private static final int CHUNK_SIZE = 1024 * 1024;

    private RestTemplate restTemplate = new RestTemplate();

    public void uploadAllChunks(MultipartFile file, String fileType) throws IOException {
        long fileSize = file.getSize();
        int totalChunks = (int) Math.ceil((double) fileSize / CHUNK_SIZE);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future<?>> futures = new ArrayList<>();

        for (int chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
            final int currentChunkIndex = chunkIndex;
            Future<?> future = executorService.submit(() -> {
                try {
                    uploadChunk(file, currentChunkIndex, totalChunks, fileType);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload chunk " + currentChunkIndex, e);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Chunk upload interrupted", e);
                }
            });
            futures.add(future);
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new IOException("Chunk upload failed", e);
            }
        }

        executorService.shutdown();
    }

    private void uploadChunk(MultipartFile file, int chunkIndex, int totalChunks, String fileType)
            throws IOException, InterruptedException {
        long start = (long) chunkIndex * CHUNK_SIZE;
        long end = Math.min(start + CHUNK_SIZE, file.getSize());

        byte[] chunkData = new byte[(int) (end - start)];
        try (InputStream inputStream = file.getInputStream()) {
            inputStream.skip(start);
            inputStream.read(chunkData);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("chunk", new ByteArrayResource(chunkData) {
            @Override
            public String getFilename() {
                return fileType + "|" + file.getOriginalFilename() + "|" + chunkIndex;
            }
        });
        body.add("chunkIndex", chunkIndex);
        body.add("totalChunks", totalChunks);
        body.add("fileName", file.getOriginalFilename());
        body.add("fileType", fileType);

        System.out.println("BODY: " + body);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        int maxRetries = 3;
        int retryCount = 0;
        boolean success = false;

        while (!success && retryCount < maxRetries) {
            try {
                System.out.println("COUNT: " + retryCount);
                ResponseEntity<ChunkUploadResponse> response = restTemplate.postForEntity(
                        cdnServerUrl + "/api/uploadChunk",
                        requestEntity,
                        ChunkUploadResponse.class);

                System.out.println("RESPONSE: " + response);

                if (response.getStatusCode() == HttpStatus.OK &&
                        response.getBody() != null &&
                        response.getBody().isSuccess()) {
                    success = true;
                } else {
                    retryCount++;
                    Thread.sleep(1000 * retryCount);
                }
            } catch (Exception e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    throw new IOException("Failed to upload chunk after " + maxRetries + " retries", e);
                }
                Thread.sleep(1000 * retryCount);
            }
        }
    }
}
