package com.clinic.app.domain.usecase.patient;

import com.clinic.app.data.entities.Patient;
import com.clinic.app.domain.repository.PatientRepository;

public class GetPatientByIdUseCase {

    private final PatientRepository patientRepository;

    public GetPatientByIdUseCase(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient execute(int userId) {
        return patientRepository.getPatientById(userId);
    }
}
