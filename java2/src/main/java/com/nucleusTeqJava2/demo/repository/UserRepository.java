package com.nucleusTeqJava2.demo.repository;

import com.nucleusTeqJava2.demo.model.UserModel;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    UserModel save(UserModel user);

    Optional<UserModel> findById(int id);

    List<UserModel> findAll();

    UserModel update(UserModel user);

    boolean delete(int id);
}

@Repository
class UserRepositoryImpl implements UserRepository {
    // sample key value datastore
    private HashMap<Integer, UserModel> userStore = new HashMap<>();
    private int idCounter = 1;

    public UserRepositoryImpl() {
        initializeDummyData();
    }

    private void initializeDummyData() {
        // adding sample data
        userStore.put(1, new UserModel(1, "Aradhya Tiwari", "aradhya@example.com", "9876543210", "Engineering"));
        userStore.put(2, new UserModel(2, "John Doe", "john@example.com", "9765432109", "Product"));
        userStore.put(3, new UserModel(3, "Jane Smith", "jane@example.com", "9654321098", "Marketing"));
        idCounter = 4;
    }

    @Override
    public UserModel save(UserModel user) {
        if (user.getId() <= 0) { // this will never occur since we already have dummy data
            user.setId(idCounter++);
        }
        userStore.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<UserModel> findById(int id) {
        return Optional.ofNullable(userStore.get(id));
    }

    @Override
    public List<UserModel> findAll() {
        return new ArrayList<>(userStore.values());
    }

    @Override
    public UserModel update(UserModel user) {
        if (!userStore.containsKey(user.getId())) {
            return null;
        }
        userStore.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean delete(int id) {
        return userStore.remove(id) != null;
    }
}
