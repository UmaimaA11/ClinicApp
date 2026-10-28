package com.clinic.app.data.repository;

import android.app.Application;
import com.clinic.app.data.dao.AppointmentDao;
import com.clinic.app.data.db.ClinicDatabase;
import com.clinic.app.data.entities.Appointment;
import com.clinic.app.domain.repository.AppointmentRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppointmentRepositoryImpl implements AppointmentRepository {

    private final AppointmentDao appointmentDao;
    private final ExecutorService executorService;

    public AppointmentRepositoryImpl(Application application) {
        ClinicDatabase db = ClinicDatabase.getDatabase(application);
        appointmentDao = db.appointmentDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void insert(Appointment appointment) {
        executorService.execute(() -> appointmentDao.insert(appointment));
    }

    @Override
    public void update(Appointment appointment) {
        executorService.execute(() -> appointmentDao.update(appointment));
    }

    @Override
    public void delete(Appointment appointment) {
        executorService.execute(() -> appointmentDao.delete(appointment));
    }

    @Override
    public List<Appointment> getAppointmentsForPatient(int patientId) {
        Future<List<Appointment>> future = executorService.submit(() -> appointmentDao.getAppointmentsForPatient(patientId));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        Future<List<Appointment>> future = executorService.submit(() -> appointmentDao.getAppointmentsForDoctor(doctorId));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Appointment getAppointmentById(long appointmentId) {
        Future<Appointment> future = executorService.submit(() -> appointmentDao.getAppointmentById(appointmentId));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Appointment> getAllAppointments() {
        Future<List<Appointment>> future = executorService.submit(appointmentDao::getAllAppointments);
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
