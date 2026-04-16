package com.javaTraining.session3.repository;

import com.javaTraining.session3.model.UserModel;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private final List<UserModel> users;

    public UserRepository() {
        this.users = new ArrayList<>(List.of(
                new UserModel(1L, "Priya", 30, "USER"),
                new UserModel(2L, "Aman", 25, "ADMIN"),
                new UserModel(3L, "Rahul", 30, "USER"),
                new UserModel(4L, "Neha", 28, "USER"),
                new UserModel(5L, "Karan", 35, "MANAGER"),
                new UserModel(6L, "Meera", 22, "USER"),
                new UserModel(7L, "Arjun", 30, "ADMIN")));
    }

    public List<UserModel> findAll() {
        return new ArrayList<>(users);
    }

    public List<UserModel> search(String name, Integer age, String role) {
        return users.stream()
                // if name etc is not provided then it will be null (lhs true every element will
                // be into the result)
                // otherwise respective filter will be checked
                .filter(user -> name == null || user.getName().equalsIgnoreCase(name))
                .filter(user -> age == null || Objects.equals(user.getAge(), age))
                .filter(user -> role == null || user.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    public UserModel save(UserModel user) {
        user.setId((long) (users.size() + 1));
        users.add(user);
        return user;
    }

    public boolean deleteById(Long id) {
        return users.removeIf(user -> Objects.equals(user.getId(), id));
    }

}