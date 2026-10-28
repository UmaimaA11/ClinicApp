package com.clinic.app.domain.usecase.doctor;

import com.clinic.app.data.entities.Doctor;
import com.clinic.app.domain.repository.DoctorRepository;

public class UpdateDoctorUseCase {

    private final DoctorRepository doctorRepository;

    public UpdateDoctorUseCase(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public void execute(Doctor doctor) {
        doctorRepository.update(doctor);
    }
}
