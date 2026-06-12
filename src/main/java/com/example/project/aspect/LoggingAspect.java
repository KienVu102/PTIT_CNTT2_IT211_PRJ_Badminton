package com.example.project.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import com.example.project.dto.response.BookingResponse;
import com.example.project.dto.request.BookingRequest;
import com.example.project.repository.CourtRepository;
import com.example.project.entity.Court;

/**
 * FR-11: AOP Aspect ghi log thời gian thực hiện cho tất cả các chức năng.
 * - @Around trên Controller & Service để đo thời gian thực hiện.
 * - @AfterReturning / @AfterThrowing riêng cho booking audit log.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final CourtRepository courtRepository;

    // ========================================================================
    // FR-11: Ghi log thời gian thực hiện cho TẤT CẢ các Controller methods
    // ========================================================================
    @Around("execution(* com.example.project.controller..*(..))")
    public Object logControllerExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[CONTROLLER] >>> {}.{}() - BẮT ĐẦU", className, methodName);
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.info("[CONTROLLER] <<< {}.{}() - HOÀN THÀNH trong {} ms", className, methodName, duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("[CONTROLLER] <<< {}.{}() - THẤT BẠI sau {} ms - Lỗi: {}",
                    className, methodName, duration, ex.getMessage());
            throw ex;
        }
    }

    // ========================================================================
    // FR-11: Ghi log thời gian thực hiện cho TẤT CẢ các Service methods
    // ========================================================================
    @Around("execution(* com.example.project.service.impl..*(..))")
    public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[SERVICE] >>> {}.{}() - BẮT ĐẦU", className, methodName);
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.info("[SERVICE] <<< {}.{}() - HOÀN THÀNH trong {} ms", className, methodName, duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("[SERVICE] <<< {}.{}() - THẤT BẠI sau {} ms - Lỗi: {}",
                    className, methodName, duration, ex.getMessage());
            throw ex;
        }
    }

    // ========================================================================
    // Audit log riêng cho Booking: ghi lại chi tiết đặt sân thành công
    // ========================================================================
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

    // ========================================================================
    // Audit log riêng cho Booking: ghi lại chi tiết đặt sân thất bại
    // ========================================================================
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