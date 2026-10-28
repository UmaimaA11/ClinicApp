package com.clinic.app.domain.repository;

import com.clinic.app.data.entities.Appointment;

import java.util.List;

public interface AppointmentRepository {

    void insert(Appointment appointment);

    void update(Appointment appointment);

    void delete(Appointment appointment);

    List<Appointment> getAppointmentsForPatient(int patientId);

    List<Appointment> getAppointmentsForDoctor(int doctorId);

    Appointment getAppointmentById(long appointmentId);

    List<Appointment> getAllAppointments();
}
