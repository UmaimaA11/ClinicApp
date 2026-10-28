package com.clinic.app.domain.usecase.admin;

import com.clinic.app.data.entities.User;
import com.clinic.app.domain.repository.UserRepository;

public class CreateUserByAdminUseCase {

    private final UserRepository userRepository;

    public CreateUserByAdminUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(User user) {
        userRepository.insert(user);
    }
}
