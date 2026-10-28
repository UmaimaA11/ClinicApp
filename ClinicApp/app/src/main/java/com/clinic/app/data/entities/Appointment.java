package com.clinic.app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointments",
        indices = {@Index("patientId"), @Index("doctorId"), @Index("timeSlotId")},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "patientId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "doctorId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = TimeSlot.class,
                        parentColumns = "id",
                        childColumns = "timeSlotId",
                        onDelete = ForeignKey.RESTRICT)
        })
public class Appointment {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private long patientId;
    private int doctorId;
    private long timeSlotId;
    private String date;
    private String time;
    private String motif;

    // Constructeur utilisé par BookingActivity (5 paramètres)
    public Appointment(long patientId, int doctorId, String date, String time, String motif) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.motif = motif;
    }

    // Constructeur complet (6 paramètres)
    public Appointment(long patientId, int doctorId, String date, String time, long timeSlotId, String motif) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.timeSlotId = timeSlotId;
        this.motif = motif;
    }

    public Appointment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public long getPatientId() { return patientId; }
    public void setPatientId(long patientId) { this.patientId = patientId; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public long getTimeSlotId() { return timeSlotId; }
    public void setTimeSlotId(long timeSlotId) { this.timeSlotId = timeSlotId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
}