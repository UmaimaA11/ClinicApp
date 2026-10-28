package com.clinic.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.clinic.app.R;
import com.clinic.app.data.entities.Appointment;
import com.clinic.app.data.repository.AppointmentRepositoryImpl;
import com.clinic.app.domain.usecase.admin.GetAllAppointmentsUseCase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageAppointmentsActivity extends AppCompatActivity {

    private RecyclerView rvAppointments;
    private AppointmentAdapter adapter;
    private AppointmentRepositoryImpl repository;
    private GetAllAppointmentsUseCase getAllAppointmentsUseCase;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_appointments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        repository = new AppointmentRepositoryImpl(getApplication());
        getAllAppointmentsUseCase = new GetAllAppointmentsUseCase(repository);

        rvAppointments = findViewById(R.id.rv_appointments);
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(new ArrayList<>(), this::onCancelAppointment);
        rvAppointments.setAdapter(adapter);

        loadAppointments();
    }

    private void loadAppointments() {
        executor.execute(() -> {
            List<Appointment> appointments = getAllAppointmentsUseCase.execute();
            handler.post(() -> adapter.updateAppointments(appointments));
        });
    }

    private void onCancelAppointment(Appointment appt) {
        new AlertDialog.Builder(this)
            .setTitle("Annuler le rendez-vous")
            .setMessage("Voulez-vous vraiment annuler ce rendez-vous ?")
            .setPositiveButton("Oui, annuler", (dialog, which) -> {
                executor.execute(() -> {
                    repository.delete(appt);
                    handler.post(() -> {
                        Toast.makeText(this, "Rendez-vous annulé", Toast.LENGTH_SHORT).show();
                        loadAppointments();
                    });
                });
            })
            .setNegativeButton("Non", null)
            .show();
    }

    private static class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
        private List<Appointment> appointments;
        private final OnCancelListener cancelListener;

        interface OnCancelListener { void onCancel(Appointment appt); }

        AppointmentAdapter(List<Appointment> appointments, OnCancelListener cancelListener) {
            this.appointments = appointments;
            this.cancelListener = cancelListener;
        }

        void updateAppointments(List<Appointment> newAppointments) {
            this.appointments = newAppointments;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Utilisation du nouveau nom : item_appointmenty
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointmenty, parent, false);
            return new AppointmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
            Appointment appt = appointments.get(position);
            
            holder.tvPatient.setText("Patient ID: " + appt.getPatientId());
            holder.tvDoctor.setText("Docteur ID: " + appt.getDoctorId());
            holder.tvMotif.setText("Motif: " + appt.getMotif());
            holder.tvDateTime.setText("Rendez-vous #" + appt.getId());

            holder.btnCancel.setOnClickListener(v -> cancelListener.onCancel(appt));

            holder.btnMove.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "Pour déplacer, veuillez annuler et recréer (Fonctionnalité en cours)", Toast.LENGTH_LONG).show();
            });
        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }

        static class AppointmentViewHolder extends RecyclerView.ViewHolder {
            TextView tvPatient, tvDoctor, tvMotif, tvDateTime;
            Button btnCancel, btnMove;

            AppointmentViewHolder(@NonNull View itemView) {
                super(itemView);
                tvPatient = itemView.findViewById(R.id.tv_patient_name);
                tvDoctor = itemView.findViewById(R.id.tv_doctor_name);
                tvMotif = itemView.findViewById(R.id.tv_appointment_motif);
                tvDateTime = itemView.findViewById(R.id.tv_appointment_datetime);
                btnCancel = itemView.findViewById(R.id.btn_cancel_appointment);
                btnMove = itemView.findViewById(R.id.btn_move_appointment);
            }
        }
    }
}
