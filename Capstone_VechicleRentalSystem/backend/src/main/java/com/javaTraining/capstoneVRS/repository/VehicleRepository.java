package com.javaTraining.capstoneVRS.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import com.javaTraining.capstoneVRS.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVehicleName(String vehicleName);

    boolean isVehicleAvailable(String vehicleName, LocalDateTime fromDate, LocalDateTime toDate);
}
