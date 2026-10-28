package com.clinic.app.domain.usecase.authentication;

import com.clinic.app.data.entities.User;
import com.clinic.app.domain.repository.UserRepository;

public class UpdateMyProfileUseCase {

    private final UserRepository userRepository;

    public UpdateMyProfileUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(User user) {
        userRepository.update(user);
    }
}
