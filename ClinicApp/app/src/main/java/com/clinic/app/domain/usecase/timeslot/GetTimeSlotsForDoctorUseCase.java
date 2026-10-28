package com.clinic.app.domain.usecase.timeslot;

import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.domain.repository.TimeSlotRepository;

import java.util.List;

public class GetTimeSlotsForDoctorUseCase {

    private final TimeSlotRepository timeSlotRepository;

    public GetTimeSlotsForDoctorUseCase(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    public List<TimeSlot> execute(int doctorId) {
        return timeSlotRepository.getTimeSlotsForDoctor(doctorId);
    }
}
