package com.example.project.dto.response;

import com.example.project.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private LocalDate bookingDate;
    private String timeSlot;
    private BookingStatus status;
    private String courtName;
    private String username;
    private String imageUrl;
}