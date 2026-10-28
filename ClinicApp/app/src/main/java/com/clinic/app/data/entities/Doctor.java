package com.clinic.app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "doctors",
        foreignKeys = @ForeignKey(entity = User.class,
                                  parentColumns = "id",
                                  childColumns = "userId",
                                  onDelete = ForeignKey.CASCADE))
public class  Doctor {

    @PrimaryKey
    private int userId;

    private String specialty;

    public Doctor(int userId, String specialty) {
        this.userId = userId;
        this.specialty = specialty;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
