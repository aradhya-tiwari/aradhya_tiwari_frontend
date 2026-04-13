package com.nucleusTeqJava2.demo.service;

import com.nucleusTeqJava2.demo.model.UserModel;
import com.nucleusTeqJava2.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserModel createUser(UserModel user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        return userRepository.save(user);
    }

    public UserModel getUserById(int id) {
        Optional<UserModel> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with id " + id);
        }
        return user.get();
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public UserModel updateUser(UserModel user) {
        if (user.getId() <= 0) {
            throw new IllegalArgumentException("User id cannot be 0 or negative");
        }
        UserModel existingUser = getUserById(user.getId());
        return userRepository.update(user);
    }

    public void deleteUser(int id) {
        getUserById(id);
        userRepository.delete(id);
    }
}
