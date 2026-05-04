package com.javaTraining.capstoneVRS.controller;

import com.javaTraining.capstoneVRS.dto.request.BookingRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.BookingResponseDTO;
import com.javaTraining.capstoneVRS.service.BookingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequestDTO request,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            log.info("Create booking request received for userEmail={} vehicleId={} startDate={} endDate={}",
                    userEmail,
                    request.getVehicleId(),
                    request.getStartDate(),
                    request.getEndDate());
            BookingResponseDTO response = bookingService.createBooking(userEmail, request);
            log.info("Booking created bookingId={} for userEmail={}", response.getBookingId(), userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            log.warn("Create booking failed for vehicleId={} reason={}", request.getVehicleId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBookings(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            log.debug("Fetching bookings for userEmail={}", userEmail);
            List<BookingResponseDTO> response = bookingService.getMyBookings(userEmail);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            log.warn("Fetch my bookings failed reason={}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            log.info("Cancel booking request received for bookingId={} userEmail={}", bookingId, userEmail);
            BookingResponseDTO response = bookingService.cancelBooking(bookingId, userEmail);
            log.info("Booking cancelled bookingId={} userEmail={}", bookingId, userEmail);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            log.warn("Cancel booking failed for bookingId={} reason={}", bookingId, ex.getMessage());
            HttpStatus status = "Booking not found".equals(ex.getMessage()) ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings(Authentication authentication) {
        try {
            if (authentication == null || authentication.getAuthorities().stream()
                    .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                log.warn("Forbidden getAllBookings access attempt");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Forbidden"));
            }

            log.debug("Fetching all bookings for admin={}", authentication.getName());
            List<BookingResponseDTO> response = bookingService.getAllBookings();
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Fetch all bookings failed", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<?> getBookingsByVehicle(@PathVariable Long vehicleId) {
        try {
            log.debug("Fetching bookings by vehicleId={}", vehicleId);
            List<BookingResponseDTO> response = bookingService.getBookingsByVehicleId(vehicleId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            log.warn("Fetch bookings by vehicle failed for vehicleId={} reason={}", vehicleId, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/{bookingId}/rating")
    public ResponseEntity<?> updateRating(@PathVariable Long bookingId, @RequestBody Map<String, Integer> request,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Integer rating = request.get("rating");
            log.info("Update rating request received for bookingId={} userEmail={} rating={}",
                    bookingId,
                    userEmail,
                    rating);
            BookingResponseDTO response = bookingService.updateRating(bookingId, rating, userEmail);
            log.info("Rating updated for bookingId={} userEmail={}", bookingId, userEmail);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            log.warn("Update rating failed for bookingId={} reason={}", bookingId, ex.getMessage());
            HttpStatus status = "Booking not found".equals(ex.getMessage()) ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("message", ex.getMessage()));
        }
    }

}
