package com.javaTraining.capstoneVRS.dto.request;

import com.javaTraining.capstoneVRS.entity.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VehicleRequestDTO {

    @NotBlank(message = "Vehicle name is required")
    @Size(max = 120, message = "Vehicle name must be at most 120 characters")
    private String vehicleName;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotBlank(message = "Registration number is required")
    @Size(max = 50, min = 4, message = "Registration number must be at most 50 characters")
    private String registrationNumber;

    private String imgUrl;

    private Boolean availabilityStatus = true;

    private String basicDetails;

    private Boolean isActive = true;

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String url) {
        this.imgUrl = url;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Boolean getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(Boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getBasicDetails() {
        return basicDetails;
    }

    public void setBasicDetails(String basicDetails) {
        this.basicDetails = basicDetails;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
