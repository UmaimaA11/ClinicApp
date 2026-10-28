package com.clinic.app.data.db;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.clinic.app.data.converters.DateConverter;
import com.clinic.app.data.converters.RoleConverter;
import com.clinic.app.data.dao.AppointmentDao;
import com.clinic.app.data.dao.DoctorDao;
import com.clinic.app.data.dao.PatientDao;
import com.clinic.app.data.dao.TimeSlotDao;
import com.clinic.app.data.dao.UserDao;
import com.clinic.app.data.entities.Appointment;
import com.clinic.app.data.entities.Doctor;
import com.clinic.app.data.entities.Patient;
import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.data.entities.User;
import com.clinic.app.data.entities.UserRole;

import java.util.concurrent.Executors;

@Database(
        entities = {User.class, Doctor.class, Patient.class, TimeSlot.class, Appointment.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({RoleConverter.class, DateConverter.class})
public abstract class ClinicDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract DoctorDao doctorDao();
    public abstract PatientDao patientDao();
    public abstract TimeSlotDao timeSlotDao();
    public abstract AppointmentDao appointmentDao();

    private static volatile ClinicDatabase INSTANCE;

    public static ClinicDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ClinicDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    ClinicDatabase.class,
                                    "clinic_database"
                            )
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Insérer l'admin par défaut au premier lancement
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        User admin = new User("Administrateur", "admin@clinic.com", 0, "admin123", UserRole.ADMIN);
                                        getDatabase(context).userDao().insert(admin);
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
