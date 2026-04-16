package com.javaTraining.session3.service;

import com.javaTraining.session3.model.UserModel;
import com.javaTraining.session3.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserModel> searchUsers(String name, Integer age, String role) {
        // normalized entity first check if name or role exists or not
        // if exists then trim and lowecase the entity to make it case insensitive
        String normalizedName = (name != null) ? name.trim().toLowerCase() : null;
        String normalizedRole = (role != null) ? role.trim().toUpperCase() : null;

        if (normalizedName == null && age == null && normalizedRole == null) {
            return userRepository.findAll();
        }

        return userRepository.search(normalizedName, age, normalizedRole);
    }

}