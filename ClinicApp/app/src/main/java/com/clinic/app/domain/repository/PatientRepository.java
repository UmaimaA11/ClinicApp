package com.clinic.app.domain.repository;

import com.clinic.app.data.entities.Patient;

public interface PatientRepository {

    void insert(Patient patient);

    void update(Patient patient);

    void delete(Patient patient);

    Patient getPatientById(int userId);
}
