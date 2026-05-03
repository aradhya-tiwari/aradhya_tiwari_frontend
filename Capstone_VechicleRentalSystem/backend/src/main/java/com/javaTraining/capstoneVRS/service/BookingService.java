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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

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
            log.warn("Create booking rejected due to invalid date range userEmail={} startDate={} endDate={}",
                    userEmail,
                    request.getStartDate(),
                    request.getEndDate());
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        if (!Boolean.TRUE.equals(vehicle.getIsActive())) {
            log.warn("Create booking rejected because vehicle is inactive userEmail={} vehicleId={}",
                    userEmail,
                    vehicle.getVehicleId());
            throw new IllegalArgumentException("Vehicle is inactive");
        }

        boolean overlappingBookingExists = bookingRepository
                .existsByVehicleVehicleIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        vehicle.getVehicleId(),
                        EnumSet.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.ACTIVE),
                        request.getEndDate(),
                        request.getStartDate());

        if (overlappingBookingExists) {
            log.warn("Create booking rejected because overlapping booking exists userEmail={} vehicleId={}",
                    userEmail,
                    vehicle.getVehicleId());
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
        log.info("Booking created bookingId={} userId={} vehicleId={} startDate={} endDate={}",
                savedBooking.getBookingId(),
                user.getUserId(),
                vehicle.getVehicleId(),
                savedBooking.getStartDate(),
                savedBooking.getEndDate());
        return toResponse(savedBooking);
    }

    public List<BookingResponseDTO> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        log.debug("Loading bookings for userId={} email={}", user.getUserId(), userEmail);
        return bookingRepository.findByUserUserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .peek(this::updateBookingStatusIfNeeded)
                .map(this::toResponse)
                .toList();
    }

    public List<BookingResponseDTO> getAllBookings() {
        log.debug("Loading all bookings");
        return bookingRepository.findAll()
                .stream()
                .peek(this::updateBookingStatusIfNeeded)
                .map(this::toResponse)
                .toList();
    }

    public List<BookingResponseDTO> getBookingsByVehicleId(Long vehicleId) {
        log.debug("Loading bookings for vehicleId={}", vehicleId);
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
            log.warn("Cancel booking rejected because user does not own booking bookingId={} userId={}",
                    bookingId,
                    user.getUserId());
            throw new IllegalArgumentException("You can cancel only your own bookings");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            log.warn("Cancel booking rejected because booking already cancelled bookingId={}", bookingId);
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            log.warn("Cancel booking rejected because booking completed bookingId={}", bookingId);
            throw new IllegalArgumentException("Cannot cancel a completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(OffsetDateTime.now());
        Booking saved = bookingRepository.save(booking);
        log.info("Booking cancelled bookingId={} userId={}", bookingId, user.getUserId());

        return toResponse(saved);
    }

    public BookingResponseDTO updateRating(Long bookingId, Integer rating, String userEmail) {
        if (rating == null || rating < 0 || rating > 5) {
            log.warn("Update rating rejected because rating is invalid bookingId={} rating={}", bookingId, rating);
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            log.warn("Update rating rejected because user does not own booking bookingId={} userId={}",
                    bookingId,
                    user.getUserId());
            throw new IllegalArgumentException("You can rate only your own bookings");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED && booking.getStatus() != BookingStatus.ACTIVE) {
            log.warn("Update rating rejected because booking status is not rateable bookingId={} status={}",
                    bookingId,
                    booking.getStatus());
            throw new IllegalArgumentException("You can only rate completed or active bookings");
        }

        booking.setRating(rating);
        booking.setUpdatedAt(OffsetDateTime.now());
        Booking saved = bookingRepository.save(booking);
        log.info("Rating updated bookingId={} userId={} rating={}", bookingId, user.getUserId(), rating);

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
        dto.setRating(booking.getRating());
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
            log.info("Booking status auto-updated bookingId={} from={} to={}",
                    booking.getBookingId(),
                    booking.getStatus(),
                    expectedStatus);
            booking.setStatus(expectedStatus);
            booking.setUpdatedAt(OffsetDateTime.now());
            bookingRepository.save(booking);
        }
    }
}
