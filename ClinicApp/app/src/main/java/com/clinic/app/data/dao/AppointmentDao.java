package com.clinic.app.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.clinic.app.data.entities.Appointment;

import java.util.List;

@Dao
public interface AppointmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Appointment appointment);

    @Update
    void update(Appointment appointment);

    @Delete
    void delete(Appointment appointment);

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY id DESC")
    List<Appointment> getAppointmentsForPatient(int patientId);

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId ORDER BY id DESC")
    List<Appointment> getAppointmentsForDoctor(int doctorId);

    @Query("SELECT * FROM appointments WHERE id = :appointmentId")
    Appointment getAppointmentById(long appointmentId);

    @Query("SELECT * FROM appointments ORDER BY id DESC")
    List<Appointment> getAllAppointments();
}
