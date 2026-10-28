package com.clinic.app.domain.usecase.timeslot;

import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.domain.repository.TimeSlotRepository;

public class AddTimeSlotUseCase {

    private final TimeSlotRepository timeSlotRepository;

    public AddTimeSlotUseCase(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    public void execute(TimeSlot timeSlot) {
        timeSlotRepository.insert(timeSlot);
    }
}
