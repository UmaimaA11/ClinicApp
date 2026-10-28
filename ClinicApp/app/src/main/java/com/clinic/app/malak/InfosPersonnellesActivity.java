package com.clinic.app.malak;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.app.DatePickerDialog;
import android.widget.Toast;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.clinic.app.R;
import com.clinic.app.data.entities.Patient;
import com.clinic.app.data.entities.User;
import com.clinic.app.data.repository.PatientRepositoryImpl;
import com.clinic.app.data.repository.UserRepositoryImpl;
import com.clinic.app.domain.repository.PatientRepository;
import com.clinic.app.domain.repository.UserRepository;
import com.clinic.app.util.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class InfosPersonnellesActivity extends AppCompatActivity {

    private EditText txtNom, txtDateNaissance, txtBloodGroupView, txtAllergies, txtNumeroSecu;
    private Spinner spinnerBloodGroup;
    private ImageButton btnEditProfile;
    private boolean editMode = false;

    private SessionManager sessionManager;
    private UserRepository userRepository;
    private PatientRepository patientRepository;
    private User currentUser;
    private Patient currentPatient;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_personnelles);

        sessionManager = new SessionManager(this);
        userRepository = new UserRepositoryImpl(getApplication());
        patientRepository = new PatientRepositoryImpl(getApplication());

        // ================= LIENS AVEC LES VUES =================
        txtNom = findViewById(R.id.txtFullName);
        txtDateNaissance = findViewById(R.id.txtBirthDate);
        txtBloodGroupView = findViewById(R.id.txtBloodGroup);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        txtAllergies = findViewById(R.id.txtAllergiesCount);
        txtNumeroSecu = findViewById(R.id.txtSocialNumber);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        setupSpinners();
        setupDatePicker();

        loadUserData();

        // ================= MODE ÉDITION =================
        btnEditProfile.setOnClickListener(v -> {
            if (!editMode) {
                editMode = true;
                enableEditing(txtNom);
                // Birthdate is handled via picker
                txtDateNaissance.setEnabled(true);
                txtDateNaissance.setBackgroundResource(android.R.drawable.edit_text);

                // Switch Blood Group view to Spinner
                txtBloodGroupView.setVisibility(android.view.View.GONE);
                spinnerBloodGroup.setVisibility(android.view.View.VISIBLE);
                spinnerBloodGroup.setEnabled(true);

                enableEditing(txtAllergies);
                enableEditing(txtNumeroSecu);
                btnEditProfile.setImageResource(android.R.drawable.ic_menu_save);
            } else {
                saveUserData();
            }
        });

        // ================= BOTTOM NAVIGATION =================
        findViewById(R.id.navDashboard).setOnClickListener(v -> startActivity(
                new Intent(InfosPersonnellesActivity.this, com.clinic.app.malak.DashboardActivity.class)));

        findViewById(R.id.navRdv).setOnClickListener(v -> startActivity(
                new Intent(InfosPersonnellesActivity.this, com.clinic.app.malak.activities.BookingActivity.class)));

        findViewById(R.id.navProfil).setOnClickListener(v -> {
            Toast.makeText(this, "Vous êtes déjà sur votre profil", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Erreur: session non trouvée", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        executor.execute(() -> {
            currentUser = userRepository.getUserById(userId);
            currentPatient = patientRepository.getPatientById(userId);

            handler.post(() -> {
                if (currentUser != null) {
                    txtNom.setText(currentUser.getName());
                }

                if (currentPatient != null) {
                    if (currentPatient.getDateOfBirth() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
                        txtDateNaissance.setText(sdf.format(currentPatient.getDateOfBirth()));
                    }
                    txtBloodGroupView.setText(currentPatient.getBloodType());

                    // Set spinner selection
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerBloodGroup.getAdapter();
                    int pos = adapter.getPosition(currentPatient.getBloodType());
                    if (pos >= 0)
                        spinnerBloodGroup.setSelection(pos);

                    txtAllergies.setText(currentPatient.getAllergies());
                    txtNumeroSecu.setText(currentPatient.getSocialSecurityNumber());
                } else {
                    txtDateNaissance.setText("Non renseigné");
                    txtBloodGroupView.setText("Non renseigné");
                    txtAllergies.setText("Aucune connue");
                    txtNumeroSecu.setText("");
                }
            });
        });
    }

    private void saveUserData() {
        if (currentUser != null) {
            currentUser.setName(txtNom.getText().toString());
            executor.execute(() -> userRepository.update(currentUser));
        }

        if (currentPatient == null && currentUser != null) {
            currentPatient = new Patient(currentUser.getId(), null, "",
                    spinnerBloodGroup.getSelectedItem().toString(),
                    txtAllergies.getText().toString(),
                    txtNumeroSecu.getText().toString());
            executor.execute(() -> patientRepository.insert(currentPatient));
        } else if (currentPatient != null) {
            currentPatient.setBloodType(spinnerBloodGroup.getSelectedItem().toString());
            currentPatient.setAllergies(txtAllergies.getText().toString());
            currentPatient.setSocialSecurityNumber(txtNumeroSecu.getText().toString());

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
                currentPatient.setDateOfBirth(sdf.parse(txtDateNaissance.getText().toString()));
            } catch (Exception ignored) {
            }

            executor.execute(() -> patientRepository.update(currentPatient));
        }

        // Refresh UI
        txtBloodGroupView.setText(spinnerBloodGroup.getSelectedItem().toString());
        txtBloodGroupView.setVisibility(android.view.View.VISIBLE);
        spinnerBloodGroup.setVisibility(android.view.View.GONE);
        disableEditing(txtNom);
        disableEditing(txtDateNaissance);
        disableEditing(txtAllergies);
        disableEditing(txtNumeroSecu);
        editMode = false;
        btnEditProfile.setImageResource(android.R.drawable.ic_menu_edit);

        Toast.makeText(this, "Profil sauvegardé !", Toast.LENGTH_SHORT).show();
    }

    private void setupSpinners() {
        String[] bloodGroups = { "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Non renseigné" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloodGroups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(adapter);
    }

    private void setupDatePicker() {
        txtDateNaissance.setOnClickListener(v -> {
            if (!editMode)
                return;
            Calendar cal = Calendar.getInstance();
            DatePickerDialog picker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String date = String.format(Locale.FRANCE, "%02d/%02d/%d", dayOfMonth, month + 1, year);
                txtDateNaissance.setText(date);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            picker.show();
        });
    }

    private void enableEditing(EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.setEnabled(true);
        et.setBackgroundResource(android.R.drawable.edit_text);
    }

    private void disableEditing(EditText et) {
        et.setFocusable(false);
        et.setEnabled(false);
        et.setBackground(null);
    }
}
