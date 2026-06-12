package com.example.project.controller;

import com.example.project.dto.response.CourtResponse;
import com.example.project.security.JwtRequestFilter;
import com.example.project.security.JwtUtil;
import com.example.project.service.CourtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FR-12: Unit tests cho CourtController (2 test methods)
 * Sử dụng @WebMvcTest để test controller layer mà không cần khởi động full context.
 */
@WebMvcTest(CourtController.class)
@AutoConfigureMockMvc(addFilters = false)  // Tắt Spring Security filter để test controller logic thuần túy
class CourtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourtService courtService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    @DisplayName("GET /api/v1/courts - Lấy danh sách sân thành công")
    void getAllCourts_Success() throws Exception {
        // Arrange
        CourtResponse court1 = CourtResponse.builder()
                .id(1L).name("Sân A1").location("Hà Nội")
                .description("Sân tiêu chuẩn").active(true).build();
        CourtResponse court2 = CourtResponse.builder()
                .id(2L).name("Sân B2").location("HCM")
                .description("Sân VIP").active(true).build();

        when(courtService.getAllCourts()).thenReturn(List.of(court1, court2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/courts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("Sân A1"))
                .andExpect(jsonPath("$.data[1].name").value("Sân B2"));

        verify(courtService).getAllCourts();
    }

    @Test
    @DisplayName("GET /api/v1/courts/{id} - Lấy chi tiết sân theo ID thành công")
    void getCourtById_Success() throws Exception {
        // Arrange
        CourtResponse court = CourtResponse.builder()
                .id(1L).name("Sân A1").location("Hà Nội")
                .description("Sân tiêu chuẩn").active(true).build();

        when(courtService.getCourtById(1L)).thenReturn(court);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Sân A1"))
                .andExpect(jsonPath("$.data.location").value("Hà Nội"));

        verify(courtService).getCourtById(1L);
    }
}
