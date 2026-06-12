package com.example.project.service;

import com.example.project.dto.request.CourtRequest;
import com.example.project.dto.response.CourtResponse;
import com.example.project.entity.Court;
import com.example.project.repository.CourtRepository;
import com.example.project.service.impl.CourtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FR-12: Unit tests cho CourtService (2 test methods)
 */
@ExtendWith(MockitoExtension.class)
class CourtServiceTest {

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private CourtServiceImpl courtService;

    private Court court;

    @BeforeEach
    void setUp() {
        court = Court.builder()
                .id(1L)
                .name("Sân A1")
                .location("Hà Nội")
                .description("Sân cầu lông tiêu chuẩn")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("createCourt - Tạo sân mới thành công")
    void createCourt_Success() {
        // Arrange
        CourtRequest request = new CourtRequest();
        request.setName("Sân A1");
        request.setLocation("Hà Nội");
        request.setDescription("Sân cầu lông tiêu chuẩn");

        when(courtRepository.save(any(Court.class))).thenReturn(court);

        // Act
        CourtResponse response = courtService.createCourt(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Sân A1");
        assertThat(response.getLocation()).isEqualTo("Hà Nội");
        assertThat(response.isActive()).isTrue();
        verify(courtRepository, times(1)).save(any(Court.class));
    }

    @Test
    @DisplayName("deleteCourt - Xóa mềm sân thành công (soft delete)")
    void deleteCourt_SoftDelete_Success() {
        // Arrange
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(courtRepository.save(any(Court.class))).thenReturn(court);

        // Act
        courtService.deleteCourt(1L);

        // Assert
        assertThat(court.isActive()).isFalse();
        verify(courtRepository).save(court);
    }
}
