package com.example.project.controller;

import com.example.project.dto.request.CourtRequest;
import com.example.project.dto.response.ApiResponse;
import com.example.project.dto.response.CourtResponse;
import com.example.project.service.CourtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    // Public: xem danh sách sân
    @GetMapping("/api/v1/courts")
    public ResponseEntity<ApiResponse<List<CourtResponse>>> getAllCourts() {
        return ResponseEntity.ok(ApiResponse.success("Success",
                courtService.getAllCourts()));
    }

    @GetMapping("/api/v1/courts/{id}")
    public ResponseEntity<ApiResponse<CourtResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Success",
                courtService.getCourtById(id)));
    }

    // Manager: tạo sân mới
    @PostMapping("/api/v1/manager/courts")
    public ResponseEntity<ApiResponse<CourtResponse>> createCourt(
            @Valid @RequestBody CourtRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Court created successfully",
                        courtService.createCourt(request)));
    }

    // Manager: cập nhật sân
    @PutMapping("/api/v1/manager/courts/{id}")
    public ResponseEntity<ApiResponse<CourtResponse>> updateCourt(
            @PathVariable Long id,
            @Valid @RequestBody CourtRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Updated successfully",
                courtService.updateCourt(id, request)));
    }

    // Manager: xóa mềm sân
    @DeleteMapping("/api/v1/manager/courts/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully"));
    }

    // FR-09: Manager upload nhiều ảnh sân
    @PostMapping(value = "/api/v1/manager/courts/{id}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CourtResponse>> uploadImages(
            @PathVariable Long id,
            @RequestPart("files") List<MultipartFile> files) {
        return ResponseEntity.ok(ApiResponse.success("Images uploaded successfully",
                courtService.uploadCourtImages(id, files)));
    }
}