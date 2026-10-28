package com.clinic.app.domain.usecase.patient;

import com.clinic.app.data.entities.Patient;
import com.clinic.app.domain.repository.PatientRepository;

public class DeletePatientUseCase {

    private final PatientRepository patientRepository;

    public DeletePatientUseCase(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public void execute(Patient patient) {
        patientRepository.delete(patient);
    }
}
