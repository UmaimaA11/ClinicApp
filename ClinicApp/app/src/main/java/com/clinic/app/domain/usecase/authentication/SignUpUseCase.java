package com.clinic.app.domain.usecase.authentication;

import android.app.Application;
import com.clinic.app.data.entities.User;
import com.clinic.app.data.entities.UserRole;
import com.clinic.app.data.repository.UserRepositoryImpl;
import com.clinic.app.domain.model.SignUpResult;
import com.clinic.app.domain.repository.UserRepository;

public class SignUpUseCase {

    private final UserRepository userRepository;

    public SignUpUseCase(Application application) {
        this.userRepository = new UserRepositoryImpl(application);
    }

    // Modification : Ajout du paramètre UserRole role
    public SignUpResult execute(String name, String email, String password, UserRole role) {
        try {
            // Nettoyage des entrées
            String cleanEmail = email.trim().toLowerCase();
            String cleanPassword = password.trim();

            if (userRepository.findByEmail(cleanEmail) != null) {
                return SignUpResult.EMAIL_ALREADY_EXISTS;
            }

            // Utilisation du rôle passé en paramètre (DOCTOR, PATIENT, ou ADMIN)
            User newUser = new User(name, cleanEmail, 0, cleanPassword, role);
            userRepository.insert(newUser);

            return SignUpResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return SignUpResult.FAILURE;
        }
    }
}