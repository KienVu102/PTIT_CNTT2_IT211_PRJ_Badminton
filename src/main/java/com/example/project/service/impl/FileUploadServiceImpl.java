package com.example.project.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.project.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadSingle(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader()
                    .upload(file.getBytes(), ObjectUtils.asMap(
                            "folder", "badminton_courts",
                            "resource_type", "image"
                    ));
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Cloud storage service is temporarily unavailable. " +
                    "Please try again later.");
        }
    }

    @Override
    public List<String> uploadMultiple(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadSingle)
                .collect(Collectors.toList());
    }
}