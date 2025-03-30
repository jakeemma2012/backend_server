package com.ProjectMovie.Services;

import com.ProjectMovie.Interface.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String filePath = path + File.separator + fileName;
        File f = new File(path);
        if(!f.exists()){
            f.mkdir();
        }
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        String fullPath = path + File.separator + fileName;
        return new FileInputStream(fullPath);
    }

    @Override
    public boolean deleteFile(String path, String fileName) {
        boolean result = false;
        try{
            File file = new File(path + File.separator + fileName);
            result = file.delete();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List getAllFiles(String path) {
        File directory = new File(path);
        File[] files = directory.listFiles();
        return files != null ? Arrays.asList(files) : new ArrayList<>();
    }

}
