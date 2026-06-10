package com.example.project.repository;

import com.example.project.entity.Booking;
import com.example.project.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    boolean existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(
            Long courtId,
            LocalDate bookingDate,
            String timeSlot,
            List<BookingStatus> statuses
    );
}