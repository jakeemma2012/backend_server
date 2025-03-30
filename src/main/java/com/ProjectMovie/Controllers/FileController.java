package com.ProjectMovie.Controllers;

import com.ProjectMovie.Interface.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file/")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Value("${project.poster}")
    private String path;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestPart MultipartFile file) throws IOException {
            String upLoadFileName = fileService.uploadFile(path, file);
            return ResponseEntity.ok("File Updateloaded : " + upLoadFileName);
    }

    @GetMapping("/{fileName}")
    public void serverFileHandler(@PathVariable String fileName,HttpServletResponse response) throws IOException{
        InputStream resource = fileService.getResource(path, fileName);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName){
        boolean result = fileService.deleteFile(path, fileName);
        if(result){
            return ResponseEntity.ok("File deleted successfully");
        }
        return ResponseEntity.ok("File not found");
    }

    

    
}
