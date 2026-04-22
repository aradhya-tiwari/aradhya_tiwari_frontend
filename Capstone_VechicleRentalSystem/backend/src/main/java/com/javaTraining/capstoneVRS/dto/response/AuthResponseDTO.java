package com.javaTraining.capstoneVRS.dto.response;

public class AuthResponseDTO {

    private String message;
    private UserResponseDTO user;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }
}
