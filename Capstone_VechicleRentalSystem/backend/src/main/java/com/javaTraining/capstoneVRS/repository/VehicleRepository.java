package com.javaTraining.capstoneVRS.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import com.javaTraining.capstoneVRS.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVehicleName(String vehicleName);

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

}
