package com.javaTraining.capstoneVRS.service;

import com.javaTraining.capstoneVRS.dto.request.BookingRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.BookingResponseDTO;
import com.javaTraining.capstoneVRS.entity.Booking;
import com.javaTraining.capstoneVRS.entity.BookingStatus;
import com.javaTraining.capstoneVRS.entity.User;
import com.javaTraining.capstoneVRS.entity.UserRole;
import com.javaTraining.capstoneVRS.entity.Vehicle;
import com.javaTraining.capstoneVRS.entity.VehicleType;
import com.javaTraining.capstoneVRS.repository.BookingRepository;
import com.javaTraining.capstoneVRS.repository.UserRepository;
import com.javaTraining.capstoneVRS.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    // Mocking repositories
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    private BookingService bookingService;

    // Initial setup
    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, userRepository, vehicleRepository);
    }

    @Test
    void createBooking_savesConfirmedBooking() {
        BookingRequestDTO request = buildRequest(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        // Adding sample user
        User user = buildUser(10L, "user@gmail.com");
        // Adding sample vehicle
        Vehicle vehicle = buildVehicle(1L, "Car One", "REG-100");

        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDTO response = bookingService.createBooking("user@gmail.com", request);
        // captor to capture argument of Booking class for assertion
        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        // Check if booking repository saved
        verify(bookingRepository).save(captor.capture());

        Booking savedBooking = captor.getValue();
        assertEquals(user, savedBooking.getUser());
        assertEquals(vehicle, savedBooking.getVehicle());
        assertEquals(BookingStatus.CONFIRMED, savedBooking.getStatus());
        assertEquals(request.getStartDate(), savedBooking.getStartDate());
        assertEquals(request.getEndDate(), savedBooking.getEndDate());
        assertNotNull(savedBooking.getBookingDate());

        assertEquals(10L, response.getUserId());
        assertEquals(1L, response.getVehicleId());
        assertEquals("Car One", response.getVehicleName());
        assertEquals("REG-100", response.getRegistrationNumber());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
    }

    // Test to check condition where end date is before start date
    @Test
    void createBooking_rejectsEndDateBeforeStartDate() {
        BookingRequestDTO request = buildRequest(1L, LocalDate.now().plusDays(3), LocalDate.now().plusDays(1));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking("user@gmail.com", request));

        assertEquals("End date cannot be before start date", error.getMessage());
        verify(userRepository, never()).findByEmail(any());
    }

    // Test to check if not existing user booked a vehicle
    @Test
    void createBooking_rejectsMissingUser() {
        BookingRequestDTO request = buildRequest(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.empty());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking("user@gmail.com", request));

        assertEquals("User not found", error.getMessage());
    }

    // Test to check if booked vehicle does not exist on database
    @Test
    void createBooking_rejectsMissingVehicle() {
        BookingRequestDTO request = buildRequest(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(buildUser(10L, "user@gmail.com")));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking("user@gmail.com", request));

        assertEquals("Vehicle not found", error.getMessage());
    }

    // Test to check if inactive vehicle is booked (only possible with direct api
    // call)
    @Test
    void createBooking_rejectsInactiveVehicle() {
        BookingRequestDTO request = buildRequest(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        User user = buildUser(10L, "user@gmail.com");
        Vehicle vehicle = buildVehicle(1L, "Car One", "REG-100");
        vehicle.setIsActive(false);

        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking("user@gmail.com", request));

        assertEquals("Vehicle is inactive", error.getMessage());
    }

    // Logic to test if current booking is coinciding with another booking
    @Test
    void createBooking_rejectsOverlappingBooking() {
        BookingRequestDTO request = buildRequest(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(buildUser(10L, "user@gmail.com")));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(buildVehicle(1L, "Car One", "REG-100")));
        // If true means coinciding
        when(bookingRepository.existsByVehicleVehicleIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq(1L), any(), eq(request.getEndDate()), eq(request.getStartDate())))
                .thenReturn(true);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking("user@gmail.com", request));

        assertEquals("Vehicle is already booked for the selected dates", error.getMessage());
    }

    // Test for returning all the bookings by current user
    @Test
    void getMyBookings_returnsUserBookings() {
        User user = buildUser(10L, "user@gmail.com");
        Booking booking = buildBooking(1L, user, buildVehicle(1L, "Car One", "REG-100"));
        booking.setCreatedAt(OffsetDateTime.now());

        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        when(bookingRepository.findByUserUserIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(booking));

        List<BookingResponseDTO> response = bookingService.getMyBookings("user@gmail.com");

        assertEquals(1, response.size());
        assertEquals(10L, response.get(0).getUserId());
        assertEquals("Car One", response.get(0).getVehicleName());
    }

    @Test
    void getAllBookings_returnsAllMappedBookings() {
        Booking booking = buildBooking(1L, buildUser(10L, "user@gmail.com"), buildVehicle(1L, "Car One", "REG-100"));
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingResponseDTO> response = bookingService.getAllBookings();

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getBookingId());
    }

    // Test for fetching booking through booking id
    @Test
    void getBookingsByVehicleId_returnsVehicleBookings() {
        Booking booking = buildBooking(1L, buildUser(10L, "user@gmail.com"), buildVehicle(1L, "Car One", "REG-100"));
        when(bookingRepository.findByVehicleVehicleIdOrderByStartDateAsc(1L)).thenReturn(List.of(booking));

        List<BookingResponseDTO> response = bookingService.getBookingsByVehicleId(1L);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getVehicleId());
    }

    // Test to check cancel booking logic
    @Test
    void cancelBooking_marksBookingCancelled() {
        User user = buildUser(10L, "user@gmail.com");
        Booking booking = buildBooking(1L, user, buildVehicle(1L, "Car One", "REG-100"));
        booking.setStatus(BookingStatus.CONFIRMED);

        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDTO response = bookingService.cancelBooking(1L, "user@gmail.com");

        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        verify(bookingRepository).save(booking);
    }

    // Test for rejection of booking cancellation of another user (not possible via
    // frontend)
    @Test
    void cancelBooking_rejectsOtherUsersBooking() {
        User currentUser = buildUser(10L, "user@gmail.com");
        User bookingOwner = buildUser(11L, "other@gmail.com");
        Booking booking = buildBooking(1L, bookingOwner, buildVehicle(1L, "Car One", "REG-100"));

        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(currentUser));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> bookingService.cancelBooking(1L, "user@gmail.com"));

        assertEquals("You can cancel only your own bookings", error.getMessage());
    }

    // Test to check cancellation of booking for already cancelled booking.
    @Test
    void cancelBooking_rejectsAlreadyCancelledBooking() {
        User user = buildUser(10L, "user@gmail.com");
        Booking booking = buildBooking(1L, user, buildVehicle(1L, "Car One", "REG-100"));
        booking.setStatus(BookingStatus.CANCELLED);

        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> bookingService.cancelBooking(1L, "user@gmail.com"));

        assertEquals("Booking is already cancelled", error.getMessage());
    }

    // Test to check booking cancellation when booking is not their in database
    @Test
    void cancelBooking_rejectsMissingBooking() {
        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(buildUser(10L, "user@gmail.com")));
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> bookingService.cancelBooking(1L, "user@gmail.com"));

        assertEquals("Booking not found", error.getMessage());
    }

    // Test to add or update user rating and check for validations like
    @Test
    void updateRating_validatesAndUpdatesRatingPaths() {
        User user = buildUser(10L, "user@gmail.com");
        Booking rateableBooking = buildBooking(1L, user, buildVehicle(1L, "Car One", "REG-100"));
        rateableBooking.setStatus(BookingStatus.COMPLETED);

        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(rateableBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDTO response = bookingService.updateRating(1L, 5, "user@gmail.com");

        assertEquals(5, response.getRating());
        verify(bookingRepository).save(rateableBooking);
    }

    // Test the rejection of invalid rating, if it is not in range from 0 to 5
    @Test
    void updateRating_rejectsInvalidRating() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> bookingService.updateRating(1L, 6, "user@gmail.com"));

        assertEquals("Rating must be between 0 and 5", error.getMessage());
        verify(userRepository, never()).findByEmail(any());
    }

    // Test updateBookingStatusIfNeeded() method on getAllBookings
    @Test
    void getAllBookings_updatesStatusesAndReturnsAllBookings() {
        Booking completedByDate = buildBooking(1L, buildUser(10L, "user@gmail.com"),
                buildVehicle(1L, "Car One", "REG-100"));
        completedByDate.setStatus(BookingStatus.PENDING);
        completedByDate.setStartDate(LocalDate.now().minusDays(3));
        completedByDate.setEndDate(LocalDate.now().minusDays(1));

        Booking activeBooking = buildBooking(2L, buildUser(10L, "user@gmail.com"),
                buildVehicle(2L, "Car Two", "REG-200"));
        activeBooking.setStatus(BookingStatus.PENDING);
        activeBooking.setStartDate(LocalDate.now().minusDays(1));
        activeBooking.setEndDate(LocalDate.now().plusDays(1));

        Booking futureBooking = buildBooking(3L, buildUser(10L, "user@gmail.com"),
                buildVehicle(3L, "Car Three", "REG-300"));
        futureBooking.setStatus(BookingStatus.PENDING);
        futureBooking.setStartDate(LocalDate.now().plusDays(1));
        futureBooking.setEndDate(LocalDate.now().plusDays(2));

        when(bookingRepository.findAll()).thenReturn(List.of(completedByDate, activeBooking, futureBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<BookingResponseDTO> response = bookingService.getAllBookings();

        assertEquals(3, response.size());
        verify(bookingRepository).save(completedByDate);
        verify(bookingRepository).save(activeBooking);
        verify(bookingRepository).save(futureBooking);
    }

    // Test when user is misssing and requested my bookings
    @Test
    void getMyBookings_missingUserThrows() {
        when(userRepository.findByEmail("missing@gmail.com")).thenReturn(Optional.empty());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getMyBookings("missing@gmail.com"));

        assertEquals("User not found", error.getMessage());
    }

    // Booking request DTO
    private BookingRequestDTO buildRequest(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setVehicleId(vehicleId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        return request;
    }

    // User Creation
    private User buildUser(Long id, String email) {
        User user = new User();
        user.setUserId(id);
        user.setFullName("Test User");
        user.setEmail(email);
        user.setPasswordHash("hashed-password");
        user.setRole(UserRole.USER);
        user.setIsActive(true);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        return user;
    }

    // Vehicle creation
    private Vehicle buildVehicle(Long id, String name, String regNo) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(id);
        vehicle.setVehicleName(name);
        vehicle.setVehicleType(VehicleType.CAR);
        vehicle.setRegistrationNumber(regNo);
        vehicle.setPricePerDay(500);
        vehicle.setAvailabilityStatus(true);
        vehicle.setIsActive(true);
        vehicle.setCreatedAt(OffsetDateTime.now());
        vehicle.setUpdatedAt(OffsetDateTime.now());
        return vehicle;
    }

    // booking creation
    private Booking buildBooking(Long id, User user, Vehicle vehicle) {
        Booking booking = new Booking();
        booking.setBookingId(id);
        booking.setUser(user);
        booking.setVehicle(vehicle);
        booking.setBookingDate(OffsetDateTime.now());
        booking.setStartDate(LocalDate.now().plusDays(1));
        booking.setEndDate(LocalDate.now().plusDays(2));
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(OffsetDateTime.now());
        booking.setUpdatedAt(OffsetDateTime.now());
        return booking;
    }
}