package com.example.project.service;

import com.example.project.dto.request.BookingRequest;
import com.example.project.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String username);
    List<BookingResponse> getMyBookings(String username);
    BookingResponse approveBooking(Long bookingId);
    BookingResponse rejectBooking(Long bookingId);
    List<BookingResponse> getBookings(java.time.LocalDate date, com.example.project.enums.BookingStatus status);
    BookingResponse uploadBookingBill(Long bookingId, org.springframework.web.multipart.MultipartFile file);
}