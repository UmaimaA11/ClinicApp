package com.clinic.app.domain.usecase.timeslot;

import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.domain.repository.TimeSlotRepository;

public class DeleteTimeSlotUseCase {

    private final TimeSlotRepository timeSlotRepository;

    public DeleteTimeSlotUseCase(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    public void execute(TimeSlot timeSlot) {
        timeSlotRepository.delete(timeSlot);
    }
}
