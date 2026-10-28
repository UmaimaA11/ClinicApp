package com.clinic.app.domain.usecase.admin;

import com.clinic.app.data.entities.User;
import com.clinic.app.domain.repository.UserRepository;

public class UpdateUserUseCase {

    private final UserRepository userRepository;

    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(User user) {
        userRepository.update(user);
    }
}
