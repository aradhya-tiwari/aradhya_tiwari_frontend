package com.javaTraining.capstoneVRS.controller;

import com.javaTraining.capstoneVRS.dto.request.BookingRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.BookingResponseDTO;
import com.javaTraining.capstoneVRS.entity.BookingStatus;
import com.javaTraining.capstoneVRS.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    // Mock classes
    @Mock
    private BookingService bookingService;

    private BookingController bookingController;

    // Initial setup
    @BeforeEach
    void setUp() {
        bookingController = new BookingController(bookingService);
    }

    // Test to create booking through authenticated user
    @Test
    void createBooking_returnsCreatedUsingAuthenticatedUser() {
        BookingRequestDTO request = buildBookingRequest();
        BookingResponseDTO booking = buildBookingResponse(1L);
        Authentication authentication = userAuthentication();

        when(bookingService.createBooking("user@gmail.com", request)).thenReturn(booking);

        ResponseEntity<?> response = bookingController.createBooking(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(booking, response.getBody());
    }

    // Test for checking rejection by booking service when vehicle is not available
    // to book
    @Test
    void createBooking_returnsBadRequestWhenServiceRejectsRequest() {
        BookingRequestDTO request = buildBookingRequest();
        Authentication authentication = userAuthentication();
        when(bookingService.createBooking("user@gmail.com", request))
                .thenThrow(new IllegalArgumentException("Vehicle is already booked for the selected dates"));

        ResponseEntity<?> response = bookingController.createBooking(request, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Vehicle is already booked for the selected dates", messageOf(response));
    }

    // Returns active user's booking
    @Test
    void getMyBookings_returnsAuthenticatedUsersBookings() {
        Authentication authentication = userAuthentication();
        List<BookingResponseDTO> bookings = List.of(buildBookingResponse(1L));
        when(bookingService.getMyBookings("user@gmail.com")).thenReturn(bookings);

        ResponseEntity<?> response = bookingController.getMyBookings(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
    }

    // Test to check non existing booking
    @Test
    void cancelBooking_returnsNotFoundWhenBookingDoesNotExist() {
        Authentication authentication = userAuthentication();
        when(bookingService.cancelBooking(1L, "user@gmail.com"))
                .thenThrow(new IllegalArgumentException("Booking not found"));

        ResponseEntity<?> response = bookingController.cancelBooking(1L, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Booking not found", messageOf(response));
    }

    // Check if user has canceled someone else's booking. (It can only happen via
    // direct API request, not possible with frontend)
    @Test
    void cancelBooking_returnsBadRequestForOtherErrors() {
        Authentication authentication = userAuthentication();
        when(bookingService.cancelBooking(1L, "user@gmail.com"))
                .thenThrow(new IllegalArgumentException("You can cancel only your own bookings"));

        ResponseEntity<?> response = bookingController.cancelBooking(1L, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("You can cancel only your own bookings", messageOf(response));
    }

    // Test to check restriction on USER role to get all bookings
    @Test
    void getAllBookings_returnsForbiddenForNonAdminUsers() {
        ResponseEntity<?> response = bookingController.getAllBookings(userAuthentication());

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Forbidden", messageOf(response));
    }

    // Test to get all bookings from all users
    @Test
    void getAllBookings_returnsAllBookingsForAdmins() {
        List<BookingResponseDTO> bookings = List.of(buildBookingResponse(1L));
        when(bookingService.getAllBookings()).thenReturn(bookings);

        ResponseEntity<?> response = bookingController.getAllBookings(adminAuthentication());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
    }

    @Test
    void getBookingsByVehicle_returnsVehicleBookings() {
        List<BookingResponseDTO> bookings = List.of(buildBookingResponse(1L));
        when(bookingService.getBookingsByVehicleId(10L)).thenReturn(bookings);

        ResponseEntity<?> response = bookingController.getBookingsByVehicle(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
    }

    // Test for Add rating functionality
    @Test
    void updateRating_returnsUpdatedBooking() {
        Authentication authentication = userAuthentication();
        BookingResponseDTO booking = buildBookingResponse(1L);
        Map<String, Integer> request = Map.of("rating", 5);
        when(bookingService.updateRating(1L, 5, "user@gmail.com")).thenReturn(booking);

        ResponseEntity<?> response = bookingController.updateRating(1L, request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(booking, response.getBody());
        verify(bookingService).updateRating(1L, 5, "user@gmail.com");
    }

    // Mock Booking request DTO
    private BookingRequestDTO buildBookingRequest() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setVehicleId(10L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));
        return request;
    }

    // Mock Booking response DTO
    private BookingResponseDTO buildBookingResponse(Long bookingId) {
        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId(bookingId);
        response.setUserId(1L);
        response.setUserEmail("user@gmail.com");
        response.setVehicleId(10L);
        response.setVehicleName("Car One");
        response.setRegistrationNumber("REG-100");
        response.setStartDate(LocalDate.now().plusDays(1));
        response.setEndDate(LocalDate.now().plusDays(3));
        response.setStatus(BookingStatus.CONFIRMED);
        response.setRating(5);
        return response;
    }

    // Normal user authentication
    private Authentication userAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                "user@gmail.com",
                "secret",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    // Admin aunthentication
    private Authentication adminAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                "admin@gmail.com",
                "secret",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    // Response message, used to get message given by a response
    private String messageOf(ResponseEntity<?> response) {
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        return body.get("message").toString();
    }
}
