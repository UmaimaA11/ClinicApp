package com.clinic.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clinic.app.R;
import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.domain.usecase.appointment.BookAppointmentUseCase;
import com.clinic.app.domain.usecase.timeslot.GetAvailableTimeSlotsUseCase;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookingActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerViewTimeSlots;
    private Button btnConfirmBooking;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private TimeSlotAdapter adapter;
    private MaterialToolbar toolbar;

    private GetAvailableTimeSlotsUseCase getAvailableTimeSlotsUseCase;
    private BookAppointmentUseCase bookAppointmentUseCase;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize Use Cases
        getAvailableTimeSlotsUseCase = new GetAvailableTimeSlotsUseCase(getApplication());
        bookAppointmentUseCase = new BookAppointmentUseCase(getApplication());

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        calendarView = findViewById(R.id.calendarView);
        recyclerViewTimeSlots = findViewById(R.id.recyclerViewTimeSlots);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        // Setup Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Setup RecyclerView
        recyclerViewTimeSlots.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TimeSlotAdapter(new ArrayList<>());
        recyclerViewTimeSlots.setAdapter(adapter);

        // Set listeners
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            fetchTimeSlots(calendar.getTime());
        });

        btnConfirmBooking.setOnClickListener(v -> {
            TimeSlot selectedSlot = adapter.getSelectedTimeSlot();
            if (selectedSlot != null) {
                // Using a hardcoded patientId for now. In a real app, this would come from a user session.
                long patientId = 1L; 
                confirmBooking(patientId, selectedSlot.getId());
            } else {
                Toast.makeText(BookingActivity.this, "Veuillez sélectionner un créneau.", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch time slots for the current date by default
        fetchTimeSlots(new Date(calendarView.getDate()));
    }

    private void fetchTimeSlots(Date date) {
        showLoading(true);
        executor.execute(() -> {
            List<TimeSlot> timeSlots = getAvailableTimeSlotsUseCase.execute(date);
            handler.post(() -> {
                showLoading(false);
                if (timeSlots != null && !timeSlots.isEmpty()) {
                    adapter.setTimeSlots(timeSlots);
                    tvEmptyState.setVisibility(View.GONE);
                    recyclerViewTimeSlots.setVisibility(View.VISIBLE);
                } else {
                    adapter.setTimeSlots(new ArrayList<>()); // Clear previous data
                    tvEmptyState.setVisibility(View.VISIBLE);
                    recyclerViewTimeSlots.setVisibility(View.GONE);
                }
            });
        });
    }

    private void confirmBooking(long patientId, long timeSlotId) {
        showLoading(true);
        executor.execute(() -> {
            boolean success = bookAppointmentUseCase.execute(patientId, timeSlotId);
            handler.post(() -> {
                showLoading(false);
                if (success) {
                    Toast.makeText(BookingActivity.this, "Rendez-vous confirmé avec succès !", Toast.LENGTH_LONG).show();
                    finish(); // Close the booking screen
                } else {
                    Toast.makeText(BookingActivity.this, "Erreur : Le créneau n'est plus disponible.", Toast.LENGTH_LONG).show();
                    // Refresh the time slots as the current one is now booked
                    fetchTimeSlots(new Date(calendarView.getDate()));
                }
            });
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnConfirmBooking.setEnabled(!isLoading);
        if (isLoading) {
            recyclerViewTimeSlots.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    /**
     * Adapter for the RecyclerView displaying time slots.
     */
    private static class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

        private final List<TimeSlot> timeSlots;
        private int selectedPosition = -1;
        private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        TimeSlotAdapter(List<TimeSlot> timeSlots) {
            this.timeSlots = timeSlots;
        }

        public void setTimeSlots(List<TimeSlot> newTimeSlots) {
            this.timeSlots.clear();
            this.timeSlots.addAll(newTimeSlots);
            selectedPosition = -1; // Reset selection
            notifyDataSetChanged();
        }

        public TimeSlot getSelectedTimeSlot() {
            if (selectedPosition != -1 && selectedPosition < timeSlots.size()) {
                return timeSlots.get(selectedPosition);
            }
            return null;
        }

        @NonNull
        @Override
        public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
            return new TimeSlotViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
            TimeSlot timeSlot = timeSlots.get(position);
            String timeRange = timeFormat.format(timeSlot.getStartTime()) + " - " + timeFormat.format(timeSlot.getEndTime());
            holder.tvTimeSlot.setText(timeRange);
            holder.rbSelectTime.setChecked(position == selectedPosition);

            View.OnClickListener clickListener = v -> {
                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            };

            holder.itemView.setOnClickListener(clickListener);
            holder.rbSelectTime.setOnClickListener(clickListener);
        }

        @Override
        public int getItemCount() {
            return timeSlots.size();
        }

        static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
            TextView tvTimeSlot;
            RadioButton rbSelectTime;

            TimeSlotViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTimeSlot = itemView.findViewById(R.id.tvTimeSlot);
                rbSelectTime = itemView.findViewById(R.id.rbSelectTime);
            }
        }
    }
}
