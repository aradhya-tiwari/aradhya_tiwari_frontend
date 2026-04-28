package com.javaTraining.capstoneVRS.service;

import com.javaTraining.capstoneVRS.entity.Booking;
import com.javaTraining.capstoneVRS.entity.BookingStatus;
import com.javaTraining.capstoneVRS.entity.Vehicle;
import com.javaTraining.capstoneVRS.repository.BookingRepository;
import com.javaTraining.capstoneVRS.repository.VehicleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;

@Component
public class BookingCompletionScheduler {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;

    public BookingCompletionScheduler(BookingRepository bookingRepository, VehicleRepository vehicleRepository) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
    }

    // Runs every day at 1 AM Asia/Kolkata time and closes finished bookings.
    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Kolkata")
    @Transactional
    public void completeFinishedBookings() {
        LocalDate today = LocalDate.now();
        List<Booking> expiredBookings = bookingRepository.findByStatusInAndEndDateBefore(
                EnumSet.of(BookingStatus.CONFIRMED, BookingStatus.ACTIVE), today);

        if (expiredBookings.isEmpty()) {
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();

        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.COMPLETED);
            booking.setUpdatedAt(now);
            bookingRepository.save(booking);

            Vehicle vehicle = booking.getVehicle();
            if (vehicle != null) {
                vehicle.setAvailabilityStatus(true);
                vehicle.setUpdatedAt(now);
                vehicleRepository.save(vehicle);
            }
        }
    }
}
