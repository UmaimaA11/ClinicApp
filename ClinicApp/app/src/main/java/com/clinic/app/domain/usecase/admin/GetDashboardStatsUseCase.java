package com.clinic.app.domain.usecase.admin;

import com.clinic.app.data.entities.UserRole;
import com.clinic.app.domain.model.DashboardStats;
import com.clinic.app.domain.repository.UserRepository;

public class GetDashboardStatsUseCase {

    private final UserRepository userRepository;

    public GetDashboardStatsUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public DashboardStats execute() {
        return new DashboardStats(
                userRepository.countUsersByRole(UserRole.PATIENT),
                userRepository.countUsersByRole(UserRole.DOCTOR)
        );
    }
}
