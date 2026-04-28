package com.javaTraining.capstoneVRS.controller;

import com.javaTraining.capstoneVRS.dto.request.BookingRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.BookingResponseDTO;
import com.javaTraining.capstoneVRS.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequestDTO request,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            BookingResponseDTO response = bookingService.createBooking(userEmail, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBookings(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            List<BookingResponseDTO> response = bookingService.getMyBookings(userEmail);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            BookingResponseDTO response = bookingService.cancelBooking(bookingId, userEmail);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
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
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Forbidden"));
            }

            List<BookingResponseDTO> response = bookingService.getAllBookings();
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ex.getMessage()));
        }
    }
}
