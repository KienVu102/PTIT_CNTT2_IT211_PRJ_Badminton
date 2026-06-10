package com.example.project.controller;

import com.example.project.dto.request.BookingRequest;
import com.example.project.dto.response.ApiResponse;
import com.example.project.dto.response.BookingResponse;
import com.example.project.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // FR-06: Đặt lịch sân
    @PostMapping("/api/v1/customer/bookings")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookingResponse response = bookingService.createBooking(
                request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created successfully", response));
    }

    // FR-07: Xem lịch sử đặt sân
    @GetMapping("/api/v1/customer/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<BookingResponse> list = bookingService.getMyBookings(
                userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Success", list));
    }

    // FR-08: Admin/Manager duyệt lịch
    @PatchMapping("/api/v1/admin/bookings/{id}/approve")
    public ResponseEntity<ApiResponse<BookingResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Approved", bookingService.approveBooking(id)));
    }

    @PatchMapping("/api/v1/admin/bookings/{id}/reject")
    public ResponseEntity<ApiResponse<BookingResponse>> reject(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Rejected", bookingService.rejectBooking(id)));
    }

    // UC-02: Quản trị danh mục và Xử lý dữ liệu nâng cao
    @GetMapping("/api/v1/admin/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsForAdmin(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            @RequestParam(required = false) com.example.project.enums.BookingStatus status) {
        List<BookingResponse> list = bookingService.getBookings(date, status);
        return ResponseEntity.ok(ApiResponse.success("Success", list));
    }

    @GetMapping("/api/v1/manager/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsForManager(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            @RequestParam(required = false) com.example.project.enums.BookingStatus status) {
        List<BookingResponse> list = bookingService.getBookings(date, status);
        return ResponseEntity.ok(ApiResponse.success("Success", list));
    }

    // UC-05: Tải lên hình ảnh hóa đơn đặt lịch
    @PostMapping(value = "/api/v1/customer/bookings/{id}/images",
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BookingResponse>> uploadBookingImageForCustomer(
            @PathVariable Long id,
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file) {
        BookingResponse response = bookingService.uploadBookingBill(id, file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", response));
    }

    @PostMapping(value = "/api/v1/manager/bookings/{id}/images",
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BookingResponse>> uploadBookingImageForManager(
            @PathVariable Long id,
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file) {
        BookingResponse response = bookingService.uploadBookingBill(id, file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", response));
    }
}