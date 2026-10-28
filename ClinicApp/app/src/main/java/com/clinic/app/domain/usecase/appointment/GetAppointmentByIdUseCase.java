package com.clinic.app.domain.usecase.appointment;

import com.clinic.app.data.entities.Appointment;
import com.clinic.app.domain.repository.AppointmentRepository;

public class GetAppointmentByIdUseCase {

    private final AppointmentRepository appointmentRepository;

    public GetAppointmentByIdUseCase(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment execute(long appointmentId) {
        return appointmentRepository.getAppointmentById(appointmentId);
    }
}
