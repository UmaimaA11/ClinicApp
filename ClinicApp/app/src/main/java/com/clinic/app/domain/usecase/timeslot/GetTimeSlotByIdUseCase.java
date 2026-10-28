package com.clinic.app.domain.usecase.timeslot;

import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.domain.repository.TimeSlotRepository;

public class GetTimeSlotByIdUseCase {

    private final TimeSlotRepository timeSlotRepository;

    public GetTimeSlotByIdUseCase(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    public TimeSlot execute(int timeSlotId) {
        return timeSlotRepository.getTimeSlotById(timeSlotId);
    }
}
