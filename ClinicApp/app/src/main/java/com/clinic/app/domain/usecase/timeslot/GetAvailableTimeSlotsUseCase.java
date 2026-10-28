package com.clinic.app.domain.usecase.timeslot;

import android.app.Application;

import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.domain.repository.TimeSlotRepository;
import com.clinic.app.data.repository.TimeSlotRepositoryImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetAvailableTimeSlotsUseCase {
    private final TimeSlotRepository timeSlotRepository;

    public GetAvailableTimeSlotsUseCase(Application application) {
        this.timeSlotRepository = new TimeSlotRepositoryImpl(application);
    }

    /**
     * Executes the use case to get all available (not booked) time slots for a specific date.
     * @param date The date to check for available time slots.
     * @return A list of available TimeSlot objects.
     */
    public List<TimeSlot> execute(Date date) {
        // This is a simplified example. In a real app, you would pass the date 
        // to the repository to perform a filtered query in the database.
        // For now, we get all time slots and filter them in memory.

        List<TimeSlot> allTimeSlots = timeSlotRepository.getAllTimeSlots();
        List<TimeSlot> availableSlots = new ArrayList<>();

        // Create a Calendar instance to compare dates without time
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        cal1.setTime(date);

        java.util.Calendar cal2 = java.util.Calendar.getInstance();

        for (TimeSlot slot : allTimeSlots) {
            cal2.setTime(slot.getStartTime());

            boolean isSameDay = cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR);

            if (isSameDay && !slot.isBooked()) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }
}
