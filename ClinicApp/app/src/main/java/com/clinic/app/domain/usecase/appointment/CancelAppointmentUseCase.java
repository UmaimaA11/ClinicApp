package com.clinic.app.domain.usecase.appointment;

import com.clinic.app.data.entities.Appointment;
import com.clinic.app.domain.repository.AppointmentRepository;

public class CancelAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;

    public CancelAppointmentUseCase(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public void execute(Appointment appointment) {
        appointmentRepository.delete(appointment);
    }
}
