package com.javaTraining.capstoneVRS.controller;

import com.javaTraining.capstoneVRS.dto.request.VehicleRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.VehicleResponseDTO;
import com.javaTraining.capstoneVRS.entity.VehicleType;
import com.javaTraining.capstoneVRS.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    // Mocking vehicle service
    @Mock
    private VehicleService vehicleService;

    private VehicleController vehicleController;

    // Initial setup
    @BeforeEach
    void setUp() {
        vehicleController = new VehicleController(vehicleService);
    }

    // Test for vehicle insertion
    @Test
    void createVehicle_returnsCreatedResponse() {
        VehicleRequestDTO request = buildVehicleRequest();
        VehicleResponseDTO vehicle = buildVehicleResponse(1L, "REG-100");

        when(vehicleService.createVehicle(request)).thenReturn(vehicle);

        ResponseEntity<?> response = vehicleController.createVehicle(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(vehicle, response.getBody());
    }

    // Test for checking unique vehicle registration number
    @Test
    void createVehicle_returnsBadRequestWhenServiceRejectsRequest() {
        VehicleRequestDTO request = buildVehicleRequest();
        when(vehicleService.createVehicle(request))
                .thenThrow(new IllegalArgumentException("Registration number already exists"));

        ResponseEntity<?> response = vehicleController.createVehicle(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Registration number already exists", messageOf(response));
    }

    // Test to fetch all vehicle
    @Test
    void getAllVehicles_returnsAllVehicles() {
        List<VehicleResponseDTO> vehicles = List.of(
                buildVehicleResponse(1L, "REG-100"),
                buildVehicleResponse(2L, "REG-200"));
        when(vehicleService.getAllVehicles()).thenReturn(vehicles);

        ResponseEntity<List<VehicleResponseDTO>> response = vehicleController.getAllVehicles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(vehicles, response.getBody());
    }

    // Test for vehicle when not in the database
    @Test
    void getVehicleById_returnsNotFoundWhenMissing() {
        when(vehicleService.getVehicleById(1L)).thenThrow(new IllegalArgumentException("Vehicle not found"));

        ResponseEntity<?> response = vehicleController.getVehicleById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Vehicle not found", messageOf(response));
    }

    // Test vehicle updation when vehicle doenot exists
    @Test
    void updateVehicle_returnsNotFoundWhenVehicleDoesNotExist() {
        VehicleRequestDTO request = buildVehicleRequest();
        when(vehicleService.updateVehicle(1L, request)).thenThrow(new IllegalArgumentException("Vehicle not found"));

        ResponseEntity<?> response = vehicleController.updateVehicle(1L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Vehicle not found", messageOf(response));
    }

    // Test vehicle updation when their is validation error like duplicate
    // registration number
    @Test
    void updateVehicle_returnsBadRequestForOtherValidationErrors() {
        VehicleRequestDTO request = buildVehicleRequest();
        when(vehicleService.updateVehicle(1L, request))
                .thenThrow(new IllegalArgumentException("Registration number already exists"));

        ResponseEntity<?> response = vehicleController.updateVehicle(1L, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Registration number already exists", messageOf(response));
    }

    // Test delete vehicle and check if vehicle service deletes vehicle
    @Test
    void deleteVehicle_returnsNoContentWhenDeleteSucceeds() {
        ResponseEntity<?> response = vehicleController.deleteVehicle(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(vehicleService).deleteVehicle(1L);
    }

    // Test delete vehicle when not available
    @Test
    void deleteVehicle_returnsNotFoundWhenVehicleDoesNotExist() {
        doThrow(new IllegalArgumentException("Vehicle not found")).when(vehicleService).deleteVehicle(1L);

        ResponseEntity<?> response = vehicleController.deleteVehicle(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Vehicle not found", messageOf(response));
    }

    // Mock Vehicle Request DTO
    private VehicleRequestDTO buildVehicleRequest() {
        VehicleRequestDTO request = new VehicleRequestDTO();
        request.setVehicleName("Car One");
        request.setVehicleType(VehicleType.CAR);
        request.setRegistrationNumber("REG-100");
        request.setPricePerDay(500);
        request.setAvailabilityStatus(true);
        request.setBasicDetails("Details");
        request.setImgUrl("https://example.com/car.png");
        request.setIsActive(true);
        return request;
    }

    // Mock Vehicle response DTO
    private VehicleResponseDTO buildVehicleResponse(Long id, String registrationNumber) {
        VehicleResponseDTO response = new VehicleResponseDTO();
        response.setVehicleId(id);
        response.setVehicleName("Car One");
        response.setVehicleType(VehicleType.CAR);
        response.setRegistrationNumber(registrationNumber);
        response.setPricePerDay(500);
        response.setAvailabilityStatus(true);
        response.setBasicDetails("Details");
        response.setImgUrl("https://example.com/car.png");
        response.setIsActive(true);
        return response;
    }

    private String messageOf(ResponseEntity<?> response) {
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        return body.get("message").toString();
    }
}
