package com.clinic.app.domain.usecase.doctor;

import com.clinic.app.data.entities.Doctor;
import com.clinic.app.domain.repository.DoctorRepository;

public class GetDoctorByIdUseCase {

    private final DoctorRepository doctorRepository;

    public GetDoctorByIdUseCase(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Doctor execute(int userId) {
        return doctorRepository.getDoctorById(userId);
    }
}
