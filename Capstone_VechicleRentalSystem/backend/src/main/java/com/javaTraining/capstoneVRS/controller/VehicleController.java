package com.javaTraining.capstoneVRS.controller;

import com.javaTraining.capstoneVRS.dto.request.VehicleRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.VehicleResponseDTO;
import com.javaTraining.capstoneVRS.service.VehicleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private static final Logger log = LoggerFactory.getLogger(VehicleController.class);

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<?> createVehicle(@Valid @RequestBody VehicleRequestDTO request) {
        try {
            log.info("Create vehicle request received for registrationNumber={}", request.getRegistrationNumber());
            VehicleResponseDTO response = vehicleService.createVehicle(request);
            log.info("Vehicle created with vehicleId={} registrationNumber={}",
                    response.getVehicleId(),
                    response.getRegistrationNumber());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            log.warn("Create vehicle failed for registrationNumber={} reason={}",
                    request.getRegistrationNumber(),
                    ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {
        log.debug("Fetching all vehicles");
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<?> getVehicleById(@PathVariable Long vehicleId) {
        try {
            log.debug("Fetching vehicle by vehicleId={}", vehicleId);
            return ResponseEntity.ok(vehicleService.getVehicleById(vehicleId));
        } catch (IllegalArgumentException ex) {
            log.warn("Fetch vehicle failed for vehicleId={} reason={}", vehicleId, ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<?> updateVehicle(@PathVariable Long vehicleId,
            @Valid @RequestBody VehicleRequestDTO request) {
        try {
            log.info("Update vehicle request received for vehicleId={} registrationNumber={}",
                    vehicleId,
                    request.getRegistrationNumber());
            return ResponseEntity.ok(vehicleService.updateVehicle(vehicleId, request));
        } catch (IllegalArgumentException ex) {
            log.warn("Update vehicle failed for vehicleId={} reason={}", vehicleId, ex.getMessage());
            HttpStatus status = "Vehicle not found".equals(ex.getMessage()) ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long vehicleId) {
        try {
            log.info("Delete vehicle request received for vehicleId={}", vehicleId);
            vehicleService.deleteVehicle(vehicleId);
            log.info("Vehicle deleted for vehicleId={}", vehicleId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            log.warn("Delete vehicle failed for vehicleId={} reason={}", vehicleId, ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        }
    }
}
