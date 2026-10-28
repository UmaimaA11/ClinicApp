package com.clinic.app.domain.usecase.appointment;

import com.clinic.app.data.entities.Appointment;
import com.clinic.app.domain.repository.AppointmentRepository;

public class UpdateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;

    public UpdateAppointmentUseCase(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public void execute(Appointment appointment) {
        appointmentRepository.update(appointment);
    }
}
