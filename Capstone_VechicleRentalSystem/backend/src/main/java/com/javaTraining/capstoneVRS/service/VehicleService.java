package com.javaTraining.capstoneVRS.service;

import com.javaTraining.capstoneVRS.dto.request.VehicleRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.VehicleResponseDTO;
import com.javaTraining.capstoneVRS.entity.Vehicle;
import com.javaTraining.capstoneVRS.repository.VehicleRepository;
import com.javaTraining.capstoneVRS.repository.BookingRepository;
import com.javaTraining.capstoneVRS.entity.BookingStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepo;
    private final BookingRepository bookingRepository;

    public VehicleService(VehicleRepository vehicleRepo, BookingRepository bookingRepository) {
        this.vehicleRepo = vehicleRepo;
        this.bookingRepository = bookingRepository;
    }

    public VehicleResponseDTO createVehicle(VehicleRequestDTO request) {
        vehicleRepo.findByRegistrationNumber(request.getRegistrationNumber())
                .ifPresent(v -> {
                    throw new IllegalArgumentException("Registration number already exists");
                });

        Vehicle vehicle = new Vehicle();
        applyRequestToVehicle(vehicle, request);

        OffsetDateTime now = OffsetDateTime.now();
        vehicle.setCreatedAt(now);
        vehicle.setUpdatedAt(now);

        Vehicle saved = vehicleRepo.save(vehicle);
        return toVehicleResponse(saved);
    }

    public List<VehicleResponseDTO> getAllVehicles() {
        return vehicleRepo.findAll().stream()
                .map(this::toVehicleResponse)
                .toList();
    }

    public VehicleResponseDTO getVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        return toVehicleResponse(vehicle);
    }

    public VehicleResponseDTO updateVehicle(Long vehicleId, VehicleRequestDTO request) {
        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        vehicleRepo.findByRegistrationNumber(request.getRegistrationNumber())
                .filter(existing -> !existing.getVehicleId().equals(vehicleId))
                .ifPresent(v -> {
                    throw new IllegalArgumentException("Registration number already exists");
                });

        applyRequestToVehicle(vehicle, request);
        vehicle.setUpdatedAt(OffsetDateTime.now());

        Vehicle updated = vehicleRepo.save(vehicle);
        return toVehicleResponse(updated);
    }

    public void deleteVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        // Prevent deleting vehicles that have active
        boolean hasBookings = bookingRepository.existsByVehicleVehicleIdAndStatusIn(vehicleId,
                java.util.EnumSet.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.ACTIVE));

        if (hasBookings) {
            throw new IllegalArgumentException("Cannot delete vehicle that has existing bookings");
        }

        vehicleRepo.delete(vehicle);
    }

    private void applyRequestToVehicle(Vehicle vehicle, VehicleRequestDTO request) {
        vehicle.setVehicleName(request.getVehicleName());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setPricePerDay(request.getPricePerDay());
        vehicle.setAvailabilityStatus(request.getAvailabilityStatus() == null ? true : request.getAvailabilityStatus());
        vehicle.setBasicDetails(request.getBasicDetails());
        vehicle.setImgUrl(request.getImgUrl());
        vehicle.setIsActive(request.getIsActive() == null ? true : request.getIsActive());
    }

    private VehicleResponseDTO toVehicleResponse(Vehicle vehicle) {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        dto.setVehicleId(vehicle.getVehicleId());
        dto.setVehicleName(vehicle.getVehicleName());
        dto.setVehicleType(vehicle.getVehicleType());
        dto.setRegistrationNumber(vehicle.getRegistrationNumber());
        dto.setPricePerDay(vehicle.getPricePerDay());
        dto.setAvailabilityStatus(vehicle.getAvailabilityStatus());
        dto.setBasicDetails(vehicle.getBasicDetails());
        dto.setIsActive(vehicle.getIsActive());
        dto.setImgUrl(vehicle.getImgUrl());
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setUpdatedAt(vehicle.getUpdatedAt());
        return dto;
    }
}
