package com.clinic.app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.clinic.app.data.converters.DateConverter;

import java.util.Date;

@Entity(tableName = "time_slots",
        indices = {@Index("doctorId")},
        foreignKeys = @ForeignKey(entity = User.class,
                                  parentColumns = "id",
                                  childColumns = "doctorId",
                                  onDelete = ForeignKey.CASCADE))
public class TimeSlot {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int doctorId;

    private Date startTime;

    private Date endTime;

    private boolean isBooked;

    public TimeSlot(int doctorId, Date startTime, Date endTime, boolean isBooked) {
        this.doctorId = doctorId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBooked = isBooked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}
