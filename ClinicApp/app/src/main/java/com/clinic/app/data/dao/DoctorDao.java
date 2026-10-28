package com.clinic.app.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.clinic.app.data.entities.Doctor;

import java.util.List;

@Dao
public interface DoctorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Doctor doctor);

    @Update
    void update(Doctor doctor);

    @Delete
    void delete(Doctor doctor);

    @Query("SELECT * FROM doctors WHERE userId = :userId")
    Doctor getDoctorById(int userId);

    @Query("SELECT * FROM doctors")
    List<Doctor> getAllDoctors();
}
