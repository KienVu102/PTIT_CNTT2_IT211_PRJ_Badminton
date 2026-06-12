package com.example.project.service;

import com.example.project.dto.request.BookingRequest;
import com.example.project.dto.response.BookingResponse;
import com.example.project.entity.Booking;
import com.example.project.entity.Court;
import com.example.project.entity.User;
import com.example.project.enums.BookingStatus;
import com.example.project.enums.Role;
import com.example.project.repository.BookingRepository;
import com.example.project.repository.CourtRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * FR-12: Unit tests cho BookingService (2 test methods)
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User customer;
    private Court court;
    private Booking booking;

    @BeforeEach
    void setUp() {
        customer = User.builder()
                .id(1L)
                .username("customer1")
                .email("customer1@test.com")
                .password("encoded")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        court = Court.builder()
                .id(1L)
                .name("Sân A1")
                .location("Hà Nội")
                .active(true)
                .build();

        booking = Booking.builder()
                .id(1L)
                .user(customer)
                .court(court)
                .bookingDate(LocalDate.of(2026, 6, 15))
                .timeSlot("08:00-09:00")
                .status(BookingStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("createBooking - Đặt sân thành công khi không có xung đột")
    void createBooking_Success() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setCourtId(1L);
        request.setBookingDate(LocalDate.of(2026, 6, 15));
        request.setTimeSlot("08:00-09:00");

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(customer));
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(
                eq(1L), any(), eq("08:00-09:00"), any())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // Act
        BookingResponse response = bookingService.createBooking(request, "customer1");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCourtName()).isEqualTo("Sân A1");
        assertThat(response.getUsername()).isEqualTo("customer1");
        assertThat(response.getStatus()).isEqualTo(BookingStatus.PENDING);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("createBooking - Thất bại khi khung giờ đã được đặt (xung đột lịch)")
    void createBooking_ConflictTimeSlot_ThrowsException() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setCourtId(1L);
        request.setBookingDate(LocalDate.of(2026, 6, 15));
        request.setTimeSlot("08:00-09:00");

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(customer));
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(
                eq(1L), any(), eq("08:00-09:00"), any())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.createBooking(request, "customer1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already booked");
    }
}
