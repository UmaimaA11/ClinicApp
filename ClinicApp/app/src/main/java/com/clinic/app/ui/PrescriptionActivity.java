package com.clinic.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clinic.app.R;
import com.clinic.app.data.entities.User;
import com.clinic.app.data.repository.UserRepositoryImpl;
// SUPPRIMÉ : l'import vers .adapters car tes fichiers sont dans le même dossier ui
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrescriptionActivity extends AppCompatActivity {

    private RecyclerView rvPrescriptions;
    private PrescriptionAdapter adapter;
    private UserRepositoryImpl userRepo;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);

        userRepo = new UserRepositoryImpl(getApplication());

        rvPrescriptions = findViewById(R.id.rv_prescriptions);
        if (rvPrescriptions != null) {
            rvPrescriptions.setLayoutManager(new LinearLayoutManager(this));
        }

        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadPatientsData();
    }

    private void loadPatientsData() {
        executor.execute(() -> {
            List<User> patientsList = userRepo.getAllUsers();
            mainHandler.post(() -> {
                if (patientsList != null) {
                    // Ici, PrescriptionAdapter ne sera plus rouge car il est dans le même dossier
                    adapter = new PrescriptionAdapter(patientsList);
                    if (rvPrescriptions != null) {
                        rvPrescriptions.setAdapter(adapter);
                    }

                    TextView tvLast = findViewById(R.id.tv_last_patient);
                    if (tvLast != null && !patientsList.isEmpty()) {
                        tvLast.setText(patientsList.get(0).getName());
                    }
                }
            });
        });
    }
}