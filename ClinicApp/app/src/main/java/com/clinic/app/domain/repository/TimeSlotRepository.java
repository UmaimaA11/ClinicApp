package com.clinic.app.domain.repository;

import com.clinic.app.data.entities.TimeSlot;

import java.util.List;

public interface TimeSlotRepository {

    void insert(TimeSlot timeSlot);

    void update(TimeSlot timeSlot);

    void delete(TimeSlot timeSlot);

    List<TimeSlot> getTimeSlotsForDoctor(int doctorId);

    TimeSlot getTimeSlotById(long timeSlotId);

    List<TimeSlot> getAllTimeSlots();
}
