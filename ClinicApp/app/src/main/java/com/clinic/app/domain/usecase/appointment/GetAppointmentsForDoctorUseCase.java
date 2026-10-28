package com.clinic.app.domain.usecase.appointment;

import com.clinic.app.data.entities.Appointment;
import com.clinic.app.domain.repository.AppointmentRepository;

import java.util.List;

public class GetAppointmentsForDoctorUseCase {

    private final AppointmentRepository appointmentRepository;

    public GetAppointmentsForDoctorUseCase(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> execute(int doctorId) {
        return appointmentRepository.getAppointmentsForDoctor(doctorId);
    }
}
