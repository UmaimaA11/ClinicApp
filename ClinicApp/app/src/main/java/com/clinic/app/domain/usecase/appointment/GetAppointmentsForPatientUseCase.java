package com.clinic.app.domain.usecase.appointment;

import com.clinic.app.data.entities.Appointment;
import com.clinic.app.domain.repository.AppointmentRepository;

import java.util.List;

public class GetAppointmentsForPatientUseCase {

    private final AppointmentRepository appointmentRepository;

    public GetAppointmentsForPatientUseCase(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> execute(int patientId) {
        return appointmentRepository.getAppointmentsForPatient(patientId);
    }
}
