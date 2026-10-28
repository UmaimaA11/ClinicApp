package com.clinic.app.domain.usecase.appointment;

import android.app.Application;
import com.clinic.app.data.entities.Appointment;
import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.data.repository.AppointmentRepositoryImpl;
import com.clinic.app.data.repository.TimeSlotRepositoryImpl;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookAppointmentUseCase {
    private final AppointmentRepositoryImpl appointmentRepo;
    private final TimeSlotRepositoryImpl timeRepo;

    public BookAppointmentUseCase(Application app) {
        this.appointmentRepo = new AppointmentRepositoryImpl(app);
        this.timeRepo = new TimeSlotRepositoryImpl(app);
    }

    public boolean execute(long patientId, long timeSlotId) {
        try {
            TimeSlot slot = timeRepo.getTimeSlotById(timeSlotId);
            if (slot == null || slot.isBooked()) return false;

            SimpleDateFormat sdfD = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            SimpleDateFormat sdfT = new SimpleDateFormat("HH:mm", Locale.FRANCE);

            String d = sdfD.format(slot.getStartTime());
            String t = sdfT.format(slot.getStartTime());

            // Utilise le constructeur à 6 paramètres pour garder le lien timeSlotId
            Appointment app = new Appointment(patientId, slot.getDoctorId(), d, t, timeSlotId, "Consultation");
            appointmentRepo.insert(app);

            slot.setBooked(true);
            timeRepo.update(slot);
            return true;
        } catch (Exception e) { return false; }
    }
}