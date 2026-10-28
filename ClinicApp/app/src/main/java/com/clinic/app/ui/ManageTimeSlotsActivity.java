package com.clinic.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.clinic.app.R;
import com.clinic.app.data.entities.Doctor;
import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.data.repository.DoctorRepositoryImpl;
import com.clinic.app.data.repository.TimeSlotRepositoryImpl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageTimeSlotsActivity extends AppCompatActivity {

    private RecyclerView rvTimeSlots;
    private TimeSlotAdapter adapter;
    private TimeSlotRepositoryImpl timeSlotRepository;
    private DoctorRepositoryImpl doctorRepository;
    
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_timeslots);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        timeSlotRepository = new TimeSlotRepositoryImpl(getApplication());
        doctorRepository = new DoctorRepositoryImpl(getApplication());

        rvTimeSlots = findViewById(R.id.rv_timeslots);
        rvTimeSlots.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TimeSlotAdapter(new ArrayList<>(), this::onDeleteTimeSlot, dateFormat);
        rvTimeSlots.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fab_add_timeslot);
        fabAdd.setOnClickListener(v -> showAddTimeSlotDialog());

        loadTimeSlots();
    }

    private void loadTimeSlots() {
        executor.execute(() -> {
            List<TimeSlot> slots = timeSlotRepository.getAllTimeSlots();
            handler.post(() -> adapter.updateSlots(slots));
        });
    }

    private void onDeleteTimeSlot(TimeSlot slot) {
        new AlertDialog.Builder(this)
            .setTitle("Supprimer le créneau")
            .setMessage("Voulez-vous supprimer ce créneau ?")
            .setPositiveButton("Supprimer", (dialog, which) -> {
                executor.execute(() -> {
                    timeSlotRepository.delete(slot);
                    handler.post(() -> {
                        Toast.makeText(this, "Créneau supprimé", Toast.LENGTH_SHORT).show();
                        loadTimeSlots();
                    });
                });
            })
            .setNegativeButton("Annuler", null)
            .show();
    }

    private void showAddTimeSlotDialog() {
        executor.execute(() -> {
            List<Doctor> doctors = doctorRepository.getAllDoctors();
            handler.post(() -> {
                if (doctors.isEmpty()) {
                    Toast.makeText(this, "Veuillez d'abord ajouter un médecin", Toast.LENGTH_LONG).show();
                    return;
                }
                
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_user, null);
                builder.setView(view);

                TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
                tvTitle.setText("Ajouter un Créneau");
                
                EditText etStart = view.findViewById(R.id.et_name);
                etStart.setHint("Début (format: 12/10/2025 10:30)");
                
                EditText etEnd = view.findViewById(R.id.et_email);
                etEnd.setHint("Fin (format: 12/10/2025 11:00)");
                
                Spinner spinnerDoctor = view.findViewById(R.id.spinner_role);
                ArrayAdapter<Doctor> docAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doctors);
                spinnerDoctor.setAdapter(docAdapter);

                view.findViewById(R.id.et_phone).setVisibility(View.GONE);
                view.findViewById(R.id.et_password).setVisibility(View.GONE);

                AlertDialog dialog = builder.create();
                
                view.findViewById(R.id.btn_save).setOnClickListener(v -> {
                    try {
                        Date startTime = dateFormat.parse(etStart.getText().toString());
                        Date endTime = dateFormat.parse(etEnd.getText().toString());
                        Doctor doc = (Doctor) spinnerDoctor.getSelectedItem();
                        
                        executor.execute(() -> {
                            TimeSlot newSlot = new TimeSlot(doc.getUserId(), startTime, endTime, false);
                            timeSlotRepository.insert(newSlot);
                            handler.post(() -> {
                                loadTimeSlots();
                                dialog.dismiss();
                            });
                        });
                    } catch (Exception e) {
                        Toast.makeText(this, "Format de date invalide", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
            });
        });
    }

    private static class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {
        private List<TimeSlot> slots;
        private final OnDeleteListener listener;
        private final SimpleDateFormat dateFormat;

        interface OnDeleteListener { void onDelete(TimeSlot slot); }

        TimeSlotAdapter(List<TimeSlot> slots, OnDeleteListener listener, SimpleDateFormat dateFormat) {
            this.slots = slots;
            this.listener = listener;
            this.dateFormat = dateFormat;
        }

        void updateSlots(List<TimeSlot> newSlots) {
            this.slots = newSlots;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Utilisation du nouveau layout item_time_sloty
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_sloty, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TimeSlot slot = slots.get(position);
            String status = slot.isBooked() ? "Occupé" : "Disponible";
            holder.tvTime.setText(dateFormat.format(slot.getStartTime()) + " - " + status);
            
            holder.itemView.setOnLongClickListener(v -> {
                listener.onDelete(slot);
                return true;
            });
        }

        @Override
        public int getItemCount() { return slots.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTime;
            ViewHolder(View v) {
                super(v);
                // Liaison avec l'ID correct de item_time_sloty
                tvTime = v.findViewById(R.id.tvTimeSlot);
            }
        }
    }
}
