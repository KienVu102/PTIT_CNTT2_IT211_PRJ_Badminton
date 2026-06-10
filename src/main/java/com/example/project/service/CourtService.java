package com.example.project.service;

import com.example.project.dto.request.CourtRequest;
import com.example.project.dto.response.CourtResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourtService {
    CourtResponse createCourt(CourtRequest request);
    CourtResponse updateCourt(Long id, CourtRequest request);
    void deleteCourt(Long id);
    List<CourtResponse> getAllCourts();
    CourtResponse getCourtById(Long id);
    CourtResponse uploadCourtImages(Long id, List<MultipartFile> files);
}