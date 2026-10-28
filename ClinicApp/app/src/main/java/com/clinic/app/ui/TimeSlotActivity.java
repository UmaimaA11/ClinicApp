package com.clinic.app.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clinic.app.R;
import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.data.repository.TimeSlotRepositoryImpl;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimeSlotActivity extends AppCompatActivity {

    private TextView tvSelectedDate, tvTotalSlots, tvAvailableSlots, tvBookedSlots;
    private ImageView btnBack, btnPrevDay, btnNextDay;
    private Chip chipAll, chipAvailable, chipBooked;
    private RecyclerView recyclerSlots;
    private LinearLayout emptyState;
    private FloatingActionButton fabAdd;
    private Button btnAddFirstSlot;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private TimeSlotRepositoryImpl slotRepo;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_creneaux);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRANCE);

        // Initialisation du Repository
        slotRepo = new TimeSlotRepositoryImpl(getApplication());

        initViews();
        setupClickListeners();
        updateDateDisplay();
    }

    private void initViews() {
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        btnBack = findViewById(R.id.btn_back);
        btnPrevDay = findViewById(R.id.btn_prev_day);
        btnNextDay = findViewById(R.id.btn_next_day);
        chipAll = findViewById(R.id.chip_all);
        chipAvailable = findViewById(R.id.chip_available);
        chipBooked = findViewById(R.id.chip_booked);
        tvTotalSlots = findViewById(R.id.tv_total_slots);
        tvAvailableSlots = findViewById(R.id.tv_available_slots);
        tvBookedSlots = findViewById(R.id.tv_booked_slots);
        recyclerSlots = findViewById(R.id.recycler_slots);
        emptyState = findViewById(R.id.empty_state);
        fabAdd = findViewById(R.id.fab_add);
        btnAddFirstSlot = findViewById(R.id.btn_add_first_slot);

        recyclerSlots.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnPrevDay.setOnClickListener(v -> { calendar.add(Calendar.DAY_OF_YEAR, -1); updateDateDisplay(); });
        btnNextDay.setOnClickListener(v -> { calendar.add(Calendar.DAY_OF_YEAR, 1); updateDateDisplay(); });
        tvSelectedDate.setOnClickListener(v -> showDatePicker());

        chipAll.setOnClickListener(v -> loadSlots("ALL"));
        chipAvailable.setOnClickListener(v -> loadSlots("AVAILABLE"));
        chipBooked.setOnClickListener(v -> loadSlots("BOOKED"));

        View.OnClickListener addAction = v -> showTimePickerAndSave();
        fabAdd.setOnClickListener(addAction);
        btnAddFirstSlot.setOnClickListener(addAction);
    }

    private void updateDateDisplay() {
        tvSelectedDate.setText(dateFormat.format(calendar.getTime()));
        loadSlots("ALL");
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            updateDateDisplay();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePickerAndSave() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            Calendar startCal = (Calendar) calendar.clone();
            startCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startCal.set(Calendar.MINUTE, minute);

            Calendar endCal = (Calendar) startCal.clone();
            endCal.add(Calendar.MINUTE, 30);

            // Constructeur exact : doctorId, startTime, endTime, isBooked
            TimeSlot newSlot = new TimeSlot(currentUserId, startCal.getTime(), endCal.getTime(), false);

            executor.execute(() -> {
                slotRepo.insert(newSlot);
                mainHandler.post(() -> {
                    Toast.makeText(this, "Créneau ajouté !", Toast.LENGTH_SHORT).show();
                    loadSlots("ALL");
                });
            });
        }, 9, 0, true).show();
    }

    private void loadSlots(String filter) {
        executor.execute(() -> {
            // CORRECTION ICI : Utilisation du nom exact de ton DAO/Repository
            List<TimeSlot> allSlots = slotRepo.getTimeSlotsForDoctor(currentUserId);

            mainHandler.post(() -> {
                if (allSlots == null || allSlots.isEmpty()) {
                    showEmptyState(true);
                    updateStats(0, 0, 0);
                } else {
                    filterByDateAndDisplay(allSlots, filter);
                }
            });
        });
    }

    private void filterByDateAndDisplay(List<TimeSlot> dbList, String filter) {
        List<TimeSlot> filteredList = new ArrayList<>();
        int available = 0, booked = 0;

        Calendar target = (Calendar) calendar.clone();

        for (TimeSlot slot : dbList) {
            Calendar slotCal = Calendar.getInstance();
            slotCal.setTime(slot.getStartTime());

            // Filtrage manuel par date (car le DAO ne le fait pas)
            boolean isSameDay = (slotCal.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                    slotCal.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR));

            if (isSameDay) {
                if (slot.isBooked()) booked++; else available++;

                if (filter.equals("ALL")) {
                    filteredList.add(slot);
                } else if (filter.equals("AVAILABLE") && !slot.isBooked()) {
                    filteredList.add(slot);
                } else if (filter.equals("BOOKED") && slot.isBooked()) {
                    filteredList.add(slot);
                }
            }
        }

        updateStats(available + booked, available, booked);
        showEmptyState(filteredList.isEmpty());
        // Envoi de la liste filtrée à l'Adapter
        recyclerSlots.setAdapter(new TimeSlotAdapter(filteredList));
    }

    private void updateStats(int total, int available, int booked) {
        tvTotalSlots.setText(String.valueOf(total));
        tvAvailableSlots.setText(String.valueOf(available));
        tvBookedSlots.setText(String.valueOf(booked));
    }

    private void showEmptyState(boolean isEmpty) {
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerSlots.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}