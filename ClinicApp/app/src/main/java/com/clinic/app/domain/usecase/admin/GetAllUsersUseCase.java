package com.clinic.app.domain.usecase.admin;

import com.clinic.app.data.entities.User;
import com.clinic.app.domain.repository.UserRepository;

import java.util.List;

public class GetAllUsersUseCase {

    private final UserRepository userRepository;

    public GetAllUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> execute() {
        return userRepository.getAllUsers();
    }
}
