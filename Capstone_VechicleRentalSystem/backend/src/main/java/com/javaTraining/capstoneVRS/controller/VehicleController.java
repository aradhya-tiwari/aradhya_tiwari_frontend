package com.javaTraining.capstoneVRS.controller;

import com.javaTraining.capstoneVRS.dto.request.VehicleRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.VehicleResponseDTO;
import com.javaTraining.capstoneVRS.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<?> createVehicle(@Valid @RequestBody VehicleRequestDTO request) {
        try {
            VehicleResponseDTO response = vehicleService.createVehicle(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<?> getVehicleById(@PathVariable Long vehicleId) {
        try {
            return ResponseEntity.ok(vehicleService.getVehicleById(vehicleId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<?> updateVehicle(@PathVariable Long vehicleId,
            @Valid @RequestBody VehicleRequestDTO request) {
        try {
            return ResponseEntity.ok(vehicleService.updateVehicle(vehicleId, request));
        } catch (IllegalArgumentException ex) {
            HttpStatus status = "Vehicle not found".equals(ex.getMessage()) ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long vehicleId) {
        try {
            vehicleService.deleteVehicle(vehicleId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        }
    }
}
