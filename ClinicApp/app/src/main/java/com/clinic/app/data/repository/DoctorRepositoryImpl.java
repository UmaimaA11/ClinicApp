package com.clinic.app.data.repository;

import android.app.Application;
import com.clinic.app.data.dao.DoctorDao;
import com.clinic.app.data.db.ClinicDatabase;
import com.clinic.app.data.entities.Doctor;
import com.clinic.app.domain.repository.DoctorRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DoctorRepositoryImpl implements DoctorRepository {

    private final DoctorDao doctorDao;
    private final ExecutorService executorService;

    public DoctorRepositoryImpl(Application application) {
        ClinicDatabase db = ClinicDatabase.getDatabase(application);
        doctorDao = db.doctorDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void insert(Doctor doctor) {
        executorService.execute(() -> doctorDao.insert(doctor));
    }

    @Override
    public void update(Doctor doctor) {
        executorService.execute(() -> doctorDao.update(doctor));
    }

    @Override
    public void delete(Doctor doctor) {
        executorService.execute(() -> doctorDao.delete(doctor));
    }

    @Override
    public Doctor getDoctorById(int userId) {
        Future<Doctor> future = executorService.submit(() -> doctorDao.getDoctorById(userId));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Doctor> getAllDoctors() {
        Future<List<Doctor>> future = executorService.submit(doctorDao::getAllDoctors);
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
