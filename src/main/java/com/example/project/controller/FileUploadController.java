package com.example.project.controller;

import com.example.project.dto.response.ApiResponse;
import com.example.project.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// UC-05: Upload hình ảnh sân
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadFile(
            @RequestPart("file") MultipartFile file) {
        String url = fileUploadService.uploadSingle(file);
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", url));
    }
}
