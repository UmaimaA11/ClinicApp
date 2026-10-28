package com.clinic.app.domain.usecase.authentication;

import android.app.Application;

import com.clinic.app.data.entities.User;
import com.clinic.app.data.repository.UserRepositoryImpl;
import com.clinic.app.domain.repository.UserRepository;

public class SignInUseCase {

    private final UserRepository userRepository;

    public SignInUseCase(Application application) {
        this.userRepository = new UserRepositoryImpl(application);
    }

    public User execute(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
