package com.clinic.app.domain.usecase.admin;

import com.clinic.app.data.entities.Appointment;
import com.clinic.app.domain.repository.AppointmentRepository;

import java.util.List;

public class GetAllAppointmentsUseCase {

    private final AppointmentRepository appointmentRepository;

    public GetAllAppointmentsUseCase(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> execute() {
        return appointmentRepository.getAllAppointments();
    }
}
