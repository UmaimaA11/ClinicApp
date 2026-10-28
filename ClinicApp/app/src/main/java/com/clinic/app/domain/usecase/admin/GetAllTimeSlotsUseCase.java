package com.clinic.app.domain.usecase.admin;

import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.domain.repository.TimeSlotRepository;

import java.util.List;

public class GetAllTimeSlotsUseCase {


    private final TimeSlotRepository timeSlotRepository;

    public GetAllTimeSlotsUseCase(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    public List<TimeSlot> execute() {
        return timeSlotRepository.getAllTimeSlots();
    }
}
