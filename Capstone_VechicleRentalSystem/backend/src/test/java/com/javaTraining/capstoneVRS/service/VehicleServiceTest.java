package com.javaTraining.capstoneVRS.service;

import com.javaTraining.capstoneVRS.dto.request.VehicleRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.VehicleResponseDTO;
import com.javaTraining.capstoneVRS.entity.BookingStatus;
import com.javaTraining.capstoneVRS.entity.Vehicle;
import com.javaTraining.capstoneVRS.entity.VehicleType;
import com.javaTraining.capstoneVRS.repository.BookingRepository;
import com.javaTraining.capstoneVRS.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private BookingRepository bookingRepository;

    private VehicleService vehicleService;

    // Initial setup
    @BeforeEach
    void setUp() {
        vehicleService = new VehicleService(vehicleRepository, bookingRepository);
    }

    // Vehicle Insertion test
    @Test
    void createVehicle_savesVehicleAndReturnsResponse() {
        VehicleRequestDTO request = buildRequest("Car One", "REG-100", 500);

        when(vehicleRepository.findByRegistrationNumber("REG-100")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponseDTO response = vehicleService.createVehicle(request);

        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository).save(captor.capture());

        Vehicle savedVehicle = captor.getValue();
        assertEquals("Car One", savedVehicle.getVehicleName());
        assertEquals(VehicleType.CAR, savedVehicle.getVehicleType());
        assertEquals("REG-100", savedVehicle.getRegistrationNumber());
        assertEquals(500, savedVehicle.getPricePerDay());
        assertTrue(savedVehicle.getAvailabilityStatus());
        assertTrue(savedVehicle.getIsActive());
        assertNotNull(savedVehicle.getCreatedAt());
        assertNotNull(savedVehicle.getUpdatedAt());

        assertEquals("Car One", response.getVehicleName());
        assertEquals("REG-100", response.getRegistrationNumber());
        assertEquals(500, response.getPricePerDay());
    }

    // Registration number unique check
    @Test
    void createVehicle_rejectsDuplicateRegistration() {
        VehicleRequestDTO request = buildRequest("Car One", "REG-100", 500);
        when(vehicleRepository.findByRegistrationNumber("REG-100")).thenReturn(Optional.of(new Vehicle()));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> vehicleService.createVehicle(request));

        assertEquals("Registration number already exists", error.getMessage());
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void getAllVehicles_mapsAllEntries() {
        Vehicle first = buildVehicle(1L, "Car One", "REG-100", 500);
        Vehicle second = buildVehicle(2L, "Bike one", "REG-200", 200);
        when(vehicleRepository.findAll()).thenReturn(List.of(first, second));

        List<VehicleResponseDTO> response = vehicleService.getAllVehicles();

        assertEquals(2, response.size());
        assertEquals("Car One", response.get(0).getVehicleName());
        assertEquals("Bike One", response.get(1).getVehicleName());
    }

    // Vehicle exists check
    @Test
    void getVehicleById_throwsWhenMissing() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> vehicleService.getVehicleById(1L));

        assertEquals("Vehicle not found", error.getMessage());
    }

    // vehicle updation check
    @Test
    void updateVehicle_updatesExistingVehicle() {
        Vehicle existing = buildVehicle(1L, "Car One", "REG-100", 500);
        VehicleRequestDTO request = buildRequest("Updated Car", "REG-101", 650);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(vehicleRepository.findByRegistrationNumber("REG-101")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponseDTO response = vehicleService.updateVehicle(1L, request);

        assertEquals("Updated Car", response.getVehicleName());
        assertEquals("REG-101", response.getRegistrationNumber());
        assertEquals(650, response.getPricePerDay());
    }

    // Reject update request when registration number already exists
    @Test
    void updateVehicle_rejectsDuplicateRegistrationOnAnotherVehicle() {
        Vehicle existing = buildVehicle(1L, "Car One", "REG-100", 500);
        Vehicle other = buildVehicle(2L, "Car Two", "REG-101", 700);
        VehicleRequestDTO request = buildRequest("Updated Car", "REG-101", 650);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(vehicleRepository.findByRegistrationNumber("REG-101")).thenReturn(Optional.of(other));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> vehicleService.updateVehicle(1L, request));

        assertEquals("Registration number already exists", error.getMessage());
        verify(vehicleRepository, never()).save(any());
    }

    // Vehicle deletion without booking
    @Test
    void deleteVehicle_removesVehicleWithoutBookings() {
        Vehicle existing = buildVehicle(1L, "Car One", "REG-100", 500);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookingRepository.existsByVehicleVehicleIdAndStatusIn(eq(1L), any())).thenReturn(false);

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository).delete(existing);
    }

    // Delete vehicle when is already booked
    @Test
    void deleteVehicle_blocksVehicleWithBookings() {
        Vehicle existing = buildVehicle(1L, "Car One", "REG-100", 500);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookingRepository.existsByVehicleVehicleIdAndStatusIn(eq(1L), any()))
                .thenReturn(true);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> vehicleService.deleteVehicle(1L));

        assertEquals("Cannot delete vehicle that has existing bookings", error.getMessage());
        verify(vehicleRepository, never()).delete(any());
    }

    // Delete not existing record
    @Test
    void deleteVehicle_throwsWhenMissing() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> vehicleService.deleteVehicle(1L));

        assertEquals("Vehicle not found", error.getMessage());
    }

    // Vehicle Request DTO for creating vehicle request simulation request DTO
    private VehicleRequestDTO buildRequest(String name, String regNo, int pricePerDay) {
        VehicleRequestDTO request = new VehicleRequestDTO();
        request.setVehicleName(name);
        request.setVehicleType(VehicleType.CAR);
        request.setRegistrationNumber(regNo);
        request.setPricePerDay(pricePerDay);
        request.setAvailabilityStatus(true);
        request.setBasicDetails("Details");
        request.setImgUrl("https://example.com/vehicle.png");
        request.setIsActive(true);
        return request;
    }

    // Build vehicle method to create a dummy vehicle
    private Vehicle buildVehicle(Long id, String name, String regNo, int pricePerDay) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(id);
        vehicle.setVehicleName(name);
        vehicle.setVehicleType(VehicleType.CAR);
        vehicle.setRegistrationNumber(regNo);
        vehicle.setPricePerDay(pricePerDay);
        vehicle.setAvailabilityStatus(true);
        vehicle.setBasicDetails("Details");
        vehicle.setImgUrl("https://example.com/vehicle.png");
        vehicle.setIsActive(true);
        return vehicle;
    }
}