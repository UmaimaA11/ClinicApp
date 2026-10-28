package com.clinic.app.domain.usecase.admin;

import com.clinic.app.data.entities.User;
import com.clinic.app.domain.repository.UserRepository;

public class GetUserByIdUseCase {

    private final UserRepository userRepository;

    public GetUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(int userId) {
        return userRepository.getUserById(userId);
    }
}
