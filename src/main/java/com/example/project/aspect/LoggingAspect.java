package com.example.project.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import com.example.project.dto.response.BookingResponse;
import com.example.project.dto.request.BookingRequest;
import com.example.project.repository.CourtRepository;
import com.example.project.entity.Court;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final CourtRepository courtRepository;

    @AfterReturning(
            pointcut = "execution(* com.example.project.service.impl.BookingServiceImpl.createBooking(..))",
            returning = "result"
    )
    public void logBookingSuccess(JoinPoint joinPoint, Object result) {
        if (result instanceof BookingResponse response) {
            log.info("[AUDIT - SUCCESS] Khách hàng {} đặt thành công {} vào ngày {}, Khung giờ {}.",
                    response.getUsername(),
                    response.getCourtName(),
                    response.getBookingDate(),
                    response.getTimeSlot());
        }
    }

    @AfterThrowing(
            pointcut = "execution(* com.example.project.service.impl.BookingServiceImpl.createBooking(..))",
            throwing = "ex"
    )
    public void logBookingFailure(JoinPoint joinPoint, Exception ex) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length >= 2 && args[0] instanceof BookingRequest request && args[1] instanceof String username) {
            String courtName = courtRepository.findById(request.getCourtId())
                    .map(Court::getName)
                    .orElse("Sân " + request.getCourtId());
            if (ex.getMessage() != null && (ex.getMessage().contains("already booked") || ex.getMessage().contains("conflict"))) {
                log.error("[AUDIT - FAILED] Khách hàng {} cố gắng đặt {} nhưng thất bại do xung đột lịch.",
                        username, courtName);
            } else {
                log.error("[AUDIT - FAILED] Khách hàng {} cố gắng đặt {} nhưng thất bại do: {}.",
                        username, courtName, ex.getMessage());
            }
        }
    }
}