package com.example.project.service.impl;

import com.example.project.dto.request.BookingRequest;
import com.example.project.dto.response.BookingResponse;
import com.example.project.entity.Booking;
import com.example.project.entity.Court;
import com.example.project.entity.User;
import com.example.project.enums.BookingStatus;
import com.example.project.repository.BookingRepository;
import com.example.project.repository.CourtRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final com.example.project.service.FileUploadService fileUploadService;

    @Override
    public BookingResponse createBooking(BookingRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Court not found"));

        String timeSlot = request.getTimeSlotValue();
        if (timeSlot == null || timeSlot.isBlank()) {
            throw new RuntimeException("Time slot is required");
        }

        boolean isConflict = bookingRepository
                .existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(
                        request.getCourtId(),
                        request.getBookingDate(),
                        timeSlot,
                        List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)
                );

        if (isConflict)
            throw new RuntimeException("This time slot is already booked");

        Booking booking = Booking.builder()
                .user(user)
                .court(court)
                .bookingDate(request.getBookingDate())
                .timeSlot(timeSlot)
                .status(BookingStatus.PENDING)
                .build();

        Booking saved = bookingRepository.save(booking);
        return mapToResponse(saved);
    }

    @Override
    public List<BookingResponse> getMyBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(BookingStatus.CONFIRMED);
        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse rejectBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(BookingStatus.REJECTED);
        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponse> getBookings(java.time.LocalDate date, BookingStatus status) {
        return bookingRepository.findAll()
                .stream()
                .filter(b -> date == null || b.getBookingDate().equals(date))
                .filter(b -> status == null || b.getStatus() == status)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse uploadBookingBill(Long bookingId, org.springframework.web.multipart.MultipartFile file) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        String url = fileUploadService.uploadSingle(file);
        booking.setImageUrl(url);
        return mapToResponse(bookingRepository.save(booking));
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingDate(booking.getBookingDate())
                .timeSlot(booking.getTimeSlot())
                .status(booking.getStatus())
                .courtName(booking.getCourt().getName())
                .username(booking.getUser().getUsername())
                .imageUrl(booking.getImageUrl())
                .build();
    }
}