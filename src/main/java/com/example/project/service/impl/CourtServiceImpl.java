package com.example.project.service.impl;

import com.example.project.dto.request.CourtRequest;
import com.example.project.dto.response.CourtResponse;
import com.example.project.entity.Court;
import com.example.project.repository.CourtRepository;
import com.example.project.service.CourtService;
import com.example.project.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;
    private final FileUploadService fileUploadService;

    @Override
    public CourtResponse createCourt(CourtRequest request) {
        Court court = Court.builder()
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .active(true)
                .build();
        return mapToResponse(courtRepository.save(court));
    }

    @Override
    public CourtResponse updateCourt(Long id, CourtRequest request) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found"));
        if (request.getName() != null)        court.setName(request.getName());
        if (request.getLocation() != null)    court.setLocation(request.getLocation());
        if (request.getDescription() != null) court.setDescription(request.getDescription());
        return mapToResponse(courtRepository.save(court));
    }

    @Override
    public void deleteCourt(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found"));
        court.setActive(false); // soft delete
        courtRepository.save(court);
    }

    @Override
    public List<CourtResponse> getAllCourts() {
        return courtRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CourtResponse getCourtById(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found"));
        return mapToResponse(court);
    }

    // FR-09: Upload nhiều ảnh sân
    @Override
    public CourtResponse uploadCourtImages(Long id, List<MultipartFile> files) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found"));

        List<String> urls = fileUploadService.uploadMultiple(files);
        court.setImageUrl(urls.get(0));
        return mapToResponse(courtRepository.save(court));
    }

    private CourtResponse mapToResponse(Court court) {
        return CourtResponse.builder()
                .id(court.getId())
                .name(court.getName())
                .location(court.getLocation())
                .description(court.getDescription())
                .imageUrl(court.getImageUrl())
                .active(court.isActive())
                .build();
    }
}