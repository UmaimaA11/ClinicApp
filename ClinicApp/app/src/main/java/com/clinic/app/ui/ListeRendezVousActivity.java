package com.clinic.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.clinic.app.R;
import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.data.repository.TimeSlotRepositoryImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListeRendezVousActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvCount;
    private LinearLayout emptyView;
    private ImageView btnBack;
    private TimeSlotRepositoryImpl slotRepo;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private int currentDoctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_rendez_vous);

        currentDoctorId = getIntent().getIntExtra("USER_ID", -1);
        slotRepo = new TimeSlotRepositoryImpl(getApplication());

        initViews();
        loadAppointments();
    }

    private void initViews() {
        // Liaison avec les IDs de votre fichier XML activity_liste_rendez_vous.xml
        tvCount = findViewById(R.id.tv_appointment_count);
        emptyView = findViewById(R.id.empty_view);
        recyclerView = findViewById(R.id.rv_rendez_vous);
        btnBack = findViewById(R.id.btn_back);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadAppointments() {
        executor.execute(() -> {
            List<TimeSlot> allSlots = slotRepo.getTimeSlotsForDoctor(currentDoctorId);
            List<TimeSlot> displayList = new ArrayList<>();

            if (allSlots != null) {
                for (TimeSlot slot : allSlots) {
                    // MODIFICATION : Pour le test, on affiche tout.
                    // Remettez 'if (slot.isBooked())' quand vos patients réserveront vraiment.
                    displayList.add(slot);
                }
            }

            mainHandler.post(() -> {
                if (displayList.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    tvCount.setText("Aucun créneau créé");
                } else {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvCount.setText("Vous avez " + displayList.size() + " créneaux au total");

                    recyclerView.setAdapter(new AppointmentAdapter(displayList, new AppointmentAdapter.OnAppointmentActionListener() {
                        @Override
                        public void onCancel(TimeSlot slot) {
                            showCancelDialog(slot);
                        }

                        @Override
                        public void onConfirm(TimeSlot slot) {
                            confirmAppointment(slot);
                        }
                    }));
                }
            });
        });
    }

    private void confirmAppointment(TimeSlot slot) {
        executor.execute(() -> {
            slot.setBooked(true); // Simule une confirmation par le docteur
            slotRepo.update(slot);
            mainHandler.post(() -> {
                Toast.makeText(this, "Rendez-vous confirmé", Toast.LENGTH_SHORT).show();
                loadAppointments();
            });
        });
    }

    private void showCancelDialog(TimeSlot slot) {
        new AlertDialog.Builder(this)
                .setTitle("Annuler / Libérer")
                .setMessage("Voulez-vous libérer ce créneau ?")
                .setPositiveButton("Oui", (d, w) -> {
                    executor.execute(() -> {
                        slot.setBooked(false);
                        slotRepo.update(slot);
                        mainHandler.post(() -> {
                            Toast.makeText(this, "Créneau libéré", Toast.LENGTH_SHORT).show();
                            loadAppointments();
                        });
                    });
                })
                .setNegativeButton("Non", null)
                .show();
    }
}