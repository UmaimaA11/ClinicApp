package com.clinic.app.domain.repository;

import com.clinic.app.data.entities.Doctor;
import java.util.List;

public interface DoctorRepository {

    void insert(Doctor doctor);

    void update(Doctor doctor);

    void delete(Doctor doctor);

    Doctor getDoctorById(int userId);

    List<Doctor> getAllDoctors();
}
