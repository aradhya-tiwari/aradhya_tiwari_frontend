package com.javaTraining.capstoneVRS.repository;

import com.javaTraining.capstoneVRS.entity.Booking;
import com.javaTraining.capstoneVRS.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // check if in the vehicles entity has vehicleId (as we pass it) AND status enum
    // BookingStatus.
    // Start date and End date should be in between the provided date and returns
    // true or false
    boolean existsByVehicleVehicleIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long vehicleId,
            Collection<BookingStatus> statuses,
            LocalDate endDate,
            LocalDate startDate);

    List<Booking> findByUserUserIdOrderByCreatedAtDesc(Long userId);
}
