package com.clinic.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.clinic.app.R;
import com.clinic.app.data.entities.Doctor;
import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.data.entities.User;
import com.clinic.app.data.repository.DoctorRepositoryImpl;
import com.clinic.app.data.repository.TimeSlotRepositoryImpl;
import com.clinic.app.data.repository.UserRepositoryImpl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedDashboardActivity extends AppCompatActivity {

    private TextView tvDoctorName, tvSpeciality, tvDoctorId;
    private TextView tvTodayCount, tvPendingCount, tvEmergencyCount;
    private LinearLayout btnSlots, btnAppointments, btnPatients, btnHelp;
    private ImageView btnNotifications;
    private TextView btnViewAll;

    private TimeSlotRepositoryImpl slotRepo;
    private DoctorRepositoryImpl doctorRepo;
    private UserRepositoryImpl userRepo;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_dashboard);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Session expirée", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initRepositories();
        setupClickListeners();
        loadDashboardData();
    }

    private void initViews() {
        tvDoctorName = findViewById(R.id.doctor_name);
        tvSpeciality = findViewById(R.id.doctor_speciality);
        tvDoctorId = findViewById(R.id.doctor_id);
        tvTodayCount = findViewById(R.id.today_appointments);
        tvPendingCount = findViewById(R.id.pending_appointments);
        tvEmergencyCount = findViewById(R.id.emergency_count);
        btnSlots = findViewById(R.id.btn_slots);
        btnAppointments = findViewById(R.id.btn_appointments);
        btnPatients = findViewById(R.id.btn_patients);
        btnHelp = findViewById(R.id.btn_help);
        btnNotifications = findViewById(R.id.btn_notifications);
        btnViewAll = findViewById(R.id.btn_view_all);
    }

    private void initRepositories() {
        slotRepo = new TimeSlotRepositoryImpl(getApplication());
        doctorRepo = new DoctorRepositoryImpl(getApplication());
        userRepo = new UserRepositoryImpl(getApplication());
    }

    private void loadDashboardData() {
        executor.execute(() -> {
            User user = userRepo.getUserById(currentUserId);
            Doctor doctor = doctorRepo.getDoctorById(currentUserId);
            List<TimeSlot> allSlots = slotRepo.getTimeSlotsForDoctor(currentUserId);

            int reservedCount = 0;
            if (allSlots != null) {
                for (TimeSlot s : allSlots) {
                    if (s.isBooked()) reservedCount++;
                }
            }
            final int finalCount = reservedCount;

            mainHandler.post(() -> {
                if (user != null) {
                    tvDoctorName.setText("Dr. " + user.getName());
                    tvDoctorId.setText("ID: MED-" + user.getId());
                }
                if (doctor != null && doctor.getSpecialty() != null) {
                    tvSpeciality.setText(doctor.getSpecialty());
                }
                tvTodayCount.setText(String.valueOf(finalCount));
                tvPendingCount.setText(String.valueOf(finalCount));
            });
        });
    }

    private void setupClickListeners() {
        btnSlots.setOnClickListener(v -> {
            Intent intent = new Intent(this, TimeSlotActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        btnAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListeRendezVousActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        btnPatients.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrescriptionActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        btnHelp.setOnClickListener(v -> {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!executor.isShutdown()) executor.shutdown();
    }
}