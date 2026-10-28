package com.clinic.app.domain.usecase.patient;

import com.clinic.app.data.entities.Patient;
import com.clinic.app.domain.repository.PatientRepository;

public class AddPatientUseCase {

    private final PatientRepository patientRepository;

    public AddPatientUseCase(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public void execute(Patient patient) {
        patientRepository.insert(patient);
    }
}
