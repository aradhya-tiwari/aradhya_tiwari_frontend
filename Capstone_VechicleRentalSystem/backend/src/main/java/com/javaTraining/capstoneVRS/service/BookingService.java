package com.javaTraining.capstoneVRS.service;

import com.javaTraining.capstoneVRS.dto.request.BookingRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.BookingResponseDTO;
import com.javaTraining.capstoneVRS.entity.Booking;
import com.javaTraining.capstoneVRS.entity.BookingStatus;
import com.javaTraining.capstoneVRS.entity.User;
import com.javaTraining.capstoneVRS.entity.Vehicle;
import com.javaTraining.capstoneVRS.repository.BookingRepository;
import com.javaTraining.capstoneVRS.repository.UserRepository;
import com.javaTraining.capstoneVRS.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository,
            VehicleRepository vehicleRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public BookingResponseDTO createBooking(String userEmail, BookingRequestDTO request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        if (!Boolean.TRUE.equals(vehicle.getIsActive())) {
            throw new IllegalArgumentException("Vehicle is inactive");
        }

        boolean overlappingBookingExists = bookingRepository
                .existsByVehicleVehicleIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        vehicle.getVehicleId(),
                        EnumSet.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.ACTIVE),
                        request.getEndDate(),
                        request.getStartDate());

        if (overlappingBookingExists) {
            throw new IllegalArgumentException("Vehicle is already booked for the selected dates");
        }

        OffsetDateTime now = OffsetDateTime.now();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setVehicle(vehicle);
        booking.setBookingDate(now);
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(now);
        booking.setUpdatedAt(now);

        Booking savedBooking = bookingRepository.save(booking);
        return toResponse(savedBooking);
    }

    public List<BookingResponseDTO> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return bookingRepository.findByUserUserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .peek(this::updateBookingStatusIfNeeded)
                .map(this::toResponse)
                .toList();
    }

    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .peek(this::updateBookingStatusIfNeeded)
                .map(this::toResponse)
                .toList();
    }

    public List<BookingResponseDTO> getBookingsByVehicleId(Long vehicleId) {
        return bookingRepository.findByVehicleVehicleIdOrderByStartDateAsc(vehicleId)
                .stream()
                .peek(this::updateBookingStatusIfNeeded)
                .map(this::toResponse)
                .toList();
    }

    public BookingResponseDTO cancelBooking(Long bookingId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("You can cancel only your own bookings");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel a completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(OffsetDateTime.now());
        Booking saved = bookingRepository.save(booking);

        return toResponse(saved);
    }

    private BookingResponseDTO toResponse(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setUserId(booking.getUser().getUserId());
        dto.setUserEmail(booking.getUser().getEmail());
        dto.setVehicleId(booking.getVehicle().getVehicleId());
        dto.setVehicleName(booking.getVehicle().getVehicleName());
        dto.setRegistrationNumber(booking.getVehicle().getRegistrationNumber());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setStatus(booking.getStatus());
        dto.setBookingDate(booking.getBookingDate());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        return dto;
    }

    private void updateBookingStatusIfNeeded(Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
            return;
        }

        LocalDate today = LocalDate.now();
        BookingStatus expectedStatus;

        if (today.isAfter(booking.getEndDate())) {
            expectedStatus = BookingStatus.COMPLETED;
        } else if (today.isBefore(booking.getStartDate())) {
            expectedStatus = BookingStatus.CONFIRMED;
        } else {
            expectedStatus = BookingStatus.ACTIVE;
        }

        if (booking.getStatus() != expectedStatus) {
            booking.setStatus(expectedStatus);
            booking.setUpdatedAt(OffsetDateTime.now());
            bookingRepository.save(booking);
        }
    }
}
