package com.clinic.app.malak.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.clinic.app.R;
import com.clinic.app.data.repository.AppointmentRepositoryImpl;
import com.clinic.app.data.repository.DoctorRepositoryImpl;
import com.clinic.app.data.repository.UserRepositoryImpl;
import com.clinic.app.domain.repository.AppointmentRepository;
import com.clinic.app.domain.repository.DoctorRepository;
import com.clinic.app.domain.repository.UserRepository;
import com.clinic.app.malak.adapters.DoctorAdapter;
import com.clinic.app.malak.models.Doctor;
import com.clinic.app.util.SessionManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingActivity extends AppCompatActivity {
    private RecyclerView recycler;
    private DoctorAdapter adapter;
    private String selectedDate = "";
    private String selectedTime = "";
    private Doctor selectedDoctor = null;
    private List<Button> slotButtons = new ArrayList<>();
    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private UserRepository userRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prendre_rdv);

        appointmentRepository = new AppointmentRepositoryImpl(getApplication());
        doctorRepository = new DoctorRepositoryImpl(getApplication());
        userRepository = new UserRepositoryImpl(getApplication());
        sessionManager = new SessionManager(this);

        recycler = findViewById(R.id.recyclerDoctors);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        loadDoctors();
        setupCalendar();
        setupTimeSlots();

        findViewById(R.id.btnConfirm).setOnClickListener(v -> showConfirmation());

        // Navigation
        findViewById(R.id.navDashboard).setOnClickListener(v -> startActivity(new Intent(this, com.clinic.app.malak.DashboardActivity.class)));
    }

    private void loadDoctors() {
        new Thread(() -> {
            List<com.clinic.app.data.entities.Doctor> dbDocs = doctorRepository.getAllDoctors();
            List<Doctor> uiList = new ArrayList<>();
            if (dbDocs != null) {
                for (com.clinic.app.data.entities.Doctor d : dbDocs) {
                    com.clinic.app.data.entities.User u = userRepository.getUserById(d.getUserId());
                    if (u != null) uiList.add(new Doctor(d.getUserId(), u.getName(), d.getSpecialty(), android.R.drawable.sym_def_app_icon));
                }
            }
            runOnUiThread(() -> {
                if (uiList.isEmpty()) Toast.makeText(this, "Aucun médecin en base", Toast.LENGTH_SHORT).show();
                adapter = new DoctorAdapter(this, uiList, doc -> {
                    selectedDoctor = doc;
                    findViewById(R.id.layoutCreneaux).setVisibility(View.VISIBLE);
                    checkConfirmButton();
                });
                recycler.setAdapter(adapter);
            });
        }).start();
    }

    private void saveAppointment() {
        long pId = (long) sessionManager.getUserId();
        if (selectedDoctor == null) return;

        com.clinic.app.data.entities.Appointment app = new com.clinic.app.data.entities.Appointment(
                pId, selectedDoctor.getId(), selectedDate, selectedTime, "Consultation"
        );

        new Thread(() -> {
            appointmentRepository.insert(app);
            runOnUiThread(() -> {
                Toast.makeText(this, "Rendez-vous validé !", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    // --- Méthodes de gestion UI ---
    private void setupCalendar() {
        CalendarView cv = findViewById(R.id.calendarView);
        Calendar c = Calendar.getInstance();
        selectedDate = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        cv.setOnDateChangeListener((v, y, m, d) -> {
            selectedDate = d + "/" + (m + 1) + "/" + y;
            checkConfirmButton();
        });
    }

    private void setupTimeSlots() {
        GridLayout grid = findViewById(R.id.gridSlots);
        String[] hours = {"08:00", "09:00", "10:00", "11:00", "14:00", "15:00"};
        grid.removeAllViews();
        for (String h : hours) {
            Button b = new Button(this);
            b.setText(h);
            b.setOnClickListener(v -> {
                selectedTime = h;
                updateButtons(b);
                checkConfirmButton();
            });
            slotButtons.add(b);
            grid.addView(b);
        }
    }

    private void updateButtons(Button active) {
        for (Button b : slotButtons) {
            b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(b == active ? Color.CYAN : Color.LTGRAY));
        }
    }

    private void checkConfirmButton() {
        findViewById(R.id.btnConfirm).setVisibility(
                (selectedDoctor != null && !selectedTime.isEmpty()) ? View.VISIBLE : View.GONE
        );
    }

    private void showConfirmation() {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_confirm_appointment);
        ((TextView)d.findViewById(R.id.txtDetails)).setText(selectedDoctor.getName() + "\n" + selectedDate + " à " + selectedTime);
        d.findViewById(R.id.btnConfirm).setOnClickListener(v -> { saveAppointment(); d.dismiss(); });
        d.findViewById(R.id.btnCancel).setOnClickListener(v -> d.dismiss());
        d.show();
    }
}