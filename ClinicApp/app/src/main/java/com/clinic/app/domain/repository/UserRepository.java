package com.clinic.app.domain.repository;

import com.clinic.app.data.entities.User;
import com.clinic.app.data.entities.UserRole;

import java.util.List;

public interface UserRepository {

    User getUserById(int userId);

    User findByEmail(String email);

    List<User> getAllUsers();

    void insert(User user);

    void update(User user);

    void delete(User user);

    int countUsersByRole(UserRole role);
}
