package com.example.project.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {

    @NotNull(message = "Court ID is required")
    private Long courtId;

    @NotNull(message = "Booking date is required")
    @FutureOrPresent(message = "Booking date must be today or in the future")
    private LocalDate bookingDate;

    private String timeSlot;

    private String timeSlotId;

    public String getTimeSlotValue() {
        return (timeSlot != null && !timeSlot.isBlank()) ? timeSlot : timeSlotId;
    }
}