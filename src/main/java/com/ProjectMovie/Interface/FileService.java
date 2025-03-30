package com.ProjectMovie.Interface;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileService {
    String uploadFile(String path, MultipartFile file) throws IOException;

    InputStream getResource(String path, String fileName) throws FileNotFoundException;

    boolean deleteFile(String path, String fileName);

    List<File> getAllFiles(String path);
}
