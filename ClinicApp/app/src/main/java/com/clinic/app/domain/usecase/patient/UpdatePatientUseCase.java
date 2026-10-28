package com.clinic.app.domain.usecase.patient;

import com.clinic.app.data.entities.Patient;
import com.clinic.app.domain.repository.PatientRepository;

public class UpdatePatientUseCase {

    private final PatientRepository patientRepository;

    public UpdatePatientUseCase(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public void execute(Patient patient) {
        patientRepository.update(patient);
    }
}
