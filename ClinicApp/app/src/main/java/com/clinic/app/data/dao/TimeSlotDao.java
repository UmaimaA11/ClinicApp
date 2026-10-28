package com.clinic.app.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.clinic.app.data.entities.TimeSlot;

import java.util.List;

@Dao
public interface TimeSlotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TimeSlot timeSlot);

    @Update
    void update(TimeSlot timeSlot);

    @Delete
    void delete(TimeSlot timeSlot);

    @Query("SELECT * FROM time_slots WHERE doctorId = :doctorId ORDER BY startTime ASC")
    List<TimeSlot> getTimeSlotsForDoctor(int doctorId);

    @Query("SELECT * FROM time_slots WHERE id = :timeSlotId")
    TimeSlot getTimeSlotById(long timeSlotId);

    @Query("SELECT * FROM time_slots ORDER BY startTime ASC")
    List<TimeSlot> getAllTimeSlots();
}
