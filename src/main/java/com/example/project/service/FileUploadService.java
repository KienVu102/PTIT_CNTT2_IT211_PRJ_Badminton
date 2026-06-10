package com.example.project.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileUploadService {
    String uploadSingle(MultipartFile file);
    List<String> uploadMultiple(List<MultipartFile> files);
}