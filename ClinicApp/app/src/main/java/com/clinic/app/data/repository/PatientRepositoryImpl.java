package com.clinic.app.data.repository;

import android.app.Application;
import com.clinic.app.data.dao.PatientDao;
import com.clinic.app.data.db.ClinicDatabase;
import com.clinic.app.data.entities.Patient;
import com.clinic.app.domain.repository.PatientRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PatientRepositoryImpl implements PatientRepository {

    private final PatientDao patientDao;
    private final ExecutorService executorService;

    public PatientRepositoryImpl(Application application) {
        ClinicDatabase db = ClinicDatabase.getDatabase(application);
        patientDao = db.patientDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void insert(Patient patient) {
        executorService.execute(() -> patientDao.insert(patient));
    }

    @Override
    public void update(Patient patient) {
        executorService.execute(() -> patientDao.update(patient));
    }

    @Override
    public void delete(Patient patient) {
        executorService.execute(() -> patientDao.delete(patient));
    }

    @Override
    public Patient getPatientById(int userId) {
        Future<Patient> future = executorService.submit(() -> patientDao.getPatientById(userId));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
