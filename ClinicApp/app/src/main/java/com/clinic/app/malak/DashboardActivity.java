package com.clinic.app.malak;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View;

import androidx.cardview.widget.CardView;

import com.clinic.app.R;
import com.clinic.app.malak.activities.BookingActivity;
import com.clinic.app.util.SessionManager;
import com.clinic.app.domain.repository.UserRepository;
import com.clinic.app.domain.repository.PatientRepository;
import com.clinic.app.domain.repository.AppointmentRepository;
import com.clinic.app.data.repository.UserRepositoryImpl;
import com.clinic.app.data.repository.PatientRepositoryImpl;
import com.clinic.app.data.repository.AppointmentRepositoryImpl;
import com.clinic.app.domain.repository.DoctorRepository;
import com.clinic.app.data.repository.DoctorRepositoryImpl;
import com.clinic.app.data.entities.Appointment;
import com.clinic.app.data.entities.Patient;
import com.clinic.app.data.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

public class DashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private UserRepository userRepository;
    private PatientRepository patientRepository;
    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;

    private TextView txtNomUtilisateur;
    private TextView txtProchainRdvDetail;
    private TextView txtRdvAVenir, txtConsultations, txtAllergies;
    private TextView txtMedBloodGroup, txtMedAllergies, txtMedSocialNumber;
    private LinearLayout containerRdv;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);
        userRepository = new UserRepositoryImpl(getApplication());
        patientRepository = new PatientRepositoryImpl(getApplication());
        appointmentRepository = new AppointmentRepositoryImpl(getApplication());
        doctorRepository = new DoctorRepositoryImpl(getApplication());

        // ================= LIENS VUES =================
        txtNomUtilisateur = findViewById(R.id.txtNomUtilisateur);
        txtRdvAVenir = findViewById(R.id.txtRdvAVenir);
        txtConsultations = findViewById(R.id.txtConsultations);
        txtAllergies = findViewById(R.id.txtAllergies);
        containerRdv = findViewById(R.id.containerRdv);
        txtProchainRdvDetail = findViewById(R.id.txtProchainRdvDetail);
        txtMedBloodGroup = findViewById(R.id.txtMedicalBloodGroup);
        txtMedAllergies = findViewById(R.id.txtMedicalAllergies);
        txtMedSocialNumber = findViewById(R.id.txtMedicalSocialNumber);

        loadDashboardData();

        // ================= NAVIGATION =================
        // Card Prochain RDV
        CardView cardProchainRdv = findViewById(R.id.cardProchainRdv);
        cardProchainRdv.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, BookingActivity.class));
        });

        // Bottom navigation
        ImageView navDashboard = findViewById(R.id.navDashboard);
        ImageView navRdv = findViewById(R.id.navRdv);
        ImageView navProfil = findViewById(R.id.navProfil);

        navDashboard.setOnClickListener(v -> {
            // Déjà sur Dashboard, ne rien faire
        });

        navRdv.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, BookingActivity.class));
        });

        navProfil.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, com.clinic.app.malak.InfosPersonnellesActivity.class));
        });
    }

    private void loadDashboardData() {
        int userId = sessionManager.getUserId();
        String userName = sessionManager.getUserName();
        if (userId == -1) {
            finish();
            return;
        }

        txtNomUtilisateur.setText(userName);

        executor.execute(() -> {
            List<Appointment> appointments = appointmentRepository.getAppointmentsForPatient(userId);
            Patient patient = patientRepository.getPatientById(userId);
            User user = userRepository.getUserById(userId);

            handler.post(() -> {
                containerRdv.removeAllViews();
                if (appointments != null && !appointments.isEmpty()) {
                    txtRdvAVenir.setText(String.valueOf(appointments.size()));

                    // Show first appointment as "Prochain RDV"
                    Appointment next = appointments.get(0);
                    txtProchainRdvDetail
                            .setText("Le " + next.getDate() + " à " + next.getTime() + " - " + next.getMotif());

                    for (Appointment app : appointments) {
                        addAppointmentCard(app);
                    }
                } else {
                    txtRdvAVenir.setText("0");
                    txtProchainRdvDetail.setText("Aucun rendez-vous prévu");
                }

                txtConsultations.setText("0");
                int allergyCount = 0;
                if (patient != null && patient.getAllergies() != null && !patient.getAllergies().isEmpty()) {
                    if (patient.getAllergies().equalsIgnoreCase("Aucune connue")
                            || patient.getAllergies().equalsIgnoreCase("Non renseigné")) {
                        allergyCount = 0;
                    } else {
                        allergyCount = patient.getAllergies().split(",").length;
                    }
                }
                txtAllergies.setText(String.valueOf(allergyCount));

                if (patient != null) {
                    txtMedBloodGroup.setText("Groupe sanguin : "
                            + (patient.getBloodType() != null ? patient.getBloodType() : "Non renseigné"));
                    txtMedAllergies.setText("Allergies : "
                            + (patient.getAllergies() != null ? patient.getAllergies() : "Aucune connue"));
                    txtMedSocialNumber.setText("N° Sécurité Sociale : "
                            + (patient.getSocialSecurityNumber() != null ? patient.getSocialSecurityNumber()
                                    : "Non renseigné"));
                } else {
                    txtMedBloodGroup.setText("Groupe sanguin : Non renseigné");
                    txtMedAllergies.setText("Allergies : Non renseigné");
                    txtMedSocialNumber.setText("N° Sécurité Sociale : Non renseigné");
                }
            });
        });
    }

    private void addAppointmentCard(Appointment app) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) getResources().getDisplayMetrics().density * 260,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, (int) getResources().getDisplayMetrics().density * 16, 0);
        card.setLayoutParams(params);
        card.setRadius(24);
        card.setCardElevation(4);

        LinearLayout linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.VERTICAL);
        linear.setPadding(32, 32, 32, 32);

        TextView nom = new TextView(this);
        nom.setText("Docteur #" + app.getDoctorId());
        nom.setTextSize(16);
        nom.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView spec = new TextView(this);
        spec.setTextSize(14);
        spec.setTextColor(0xFF777777);

        // Fetch real info (name & specialty)
        executor.execute(() -> {
            User doctorUser = userRepository.getUserById(app.getDoctorId());
            com.clinic.app.data.entities.Doctor doctorEntity = (doctorRepository != null)
                    ? doctorRepository.getDoctorById(app.getDoctorId())
                    : null;

            handler.post(() -> {
                if (doctorUser != null) {
                    nom.setText("Dr. " + doctorUser.getName());
                }
                if (doctorEntity != null) {
                    spec.setText(doctorEntity.getSpecialty() + " - " + app.getDate() + " à " + app.getTime());
                } else {
                    spec.setText(app.getDate() + " à " + app.getTime());
                }
            });
        });

        TextView motif = new TextView(this);
        motif.setText(app.getMotif());
        motif.setTextSize(14);
        motif.setTextColor(0xFF149C9C);
        motif.setPadding(0, 8, 0, 8);

        TextView etat = new TextView(this);
        etat.setText("Confirmé");
        etat.setTextSize(12);
        etat.setTypeface(null, android.graphics.Typeface.ITALIC);
        etat.setTextColor(0xFF149C9C);

        linear.addView(nom);
        linear.addView(spec);
        linear.addView(motif);
        linear.addView(etat);
        card.addView(linear);
        containerRdv.addView(card);
    }
}
