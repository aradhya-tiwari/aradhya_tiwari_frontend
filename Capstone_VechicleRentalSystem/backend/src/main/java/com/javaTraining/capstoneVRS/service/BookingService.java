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

        if (!Boolean.TRUE.equals(vehicle.getAvailabilityStatus())) {
            throw new IllegalArgumentException("Vehicle is not available for booking");
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
        booking.setRating(0);

        Booking savedBooking = bookingRepository.save(booking);

        // Keep availability in sync so one vehicle cannot be booked twice.
        vehicle.setAvailabilityStatus(false);
        vehicle.setUpdatedAt(now);
        vehicleRepository.save(vehicle);

        return toResponse(savedBooking);
    }

    public List<BookingResponseDTO> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return bookingRepository.findByUserUserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
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

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(OffsetDateTime.now());
        Booking saved = bookingRepository.save(booking);

        Vehicle vehicle = booking.getVehicle();
        vehicle.setAvailabilityStatus(true);
        vehicle.setUpdatedAt(OffsetDateTime.now());
        vehicleRepository.save(vehicle);

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
}
