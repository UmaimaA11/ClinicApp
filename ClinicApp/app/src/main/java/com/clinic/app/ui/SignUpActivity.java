package com.clinic.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.clinic.app.R;
import com.clinic.app.data.db.ClinicDatabase;
import com.clinic.app.data.entities.Doctor;
import com.clinic.app.data.entities.Patient;
import com.clinic.app.data.entities.User;
import com.clinic.app.data.entities.UserRole;
import com.clinic.app.domain.model.SignUpResult;
import com.clinic.app.domain.usecase.authentication.SignUpUseCase;

import java.util.Date; // Crucial pour l'entité Patient
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEntry, emailEntry, passwordEntry, confirmPasswordEntry;
    private Button signupButton;
    private SignUpUseCase signUpUseCase;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpUseCase = new SignUpUseCase(getApplication());
        usernameEntry = findViewById(R.id.username);
        emailEntry = findViewById(R.id.email);
        passwordEntry = findViewById(R.id.password);
        confirmPasswordEntry = findViewById(R.id.confirmpassword);
        signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(v -> {
            String userStr = usernameEntry.getText().toString().trim();
            String email = emailEntry.getText().toString().trim().toLowerCase();
            String pass = passwordEntry.getText().toString().trim();
            String confirmPass = confirmPasswordEntry.getText().toString().trim();

            if (userStr.isEmpty() || email.isEmpty() || pass.isEmpty() || !pass.equals(confirmPass)) {
                Toast.makeText(this, "Veuillez remplir correctement les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            UserRole role = email.endsWith("@clinic.com") ? UserRole.DOCTOR : UserRole.PATIENT;

            executor.execute(() -> {
                SignUpResult result = signUpUseCase.execute(userStr, email, pass, role);

                if (result == SignUpResult.SUCCESS) {
                    ClinicDatabase db = ClinicDatabase.getDatabase(getApplicationContext());
                    User createdUser = db.userDao().findByEmail(email);

                    if (createdUser != null) {
                        if (role == UserRole.DOCTOR) {
                            // Constructeur Doctor(int userId, String specialty)
                            Doctor d = new Doctor(createdUser.getId(), "Généraliste");
                            db.doctorDao().insert(d);
                        } else {
                            // CORRECTION : Respecter le constructeur Patient de ton camarade
                            // Arguments : userId (int), dateOfBirth (Date), gender (String),
                            // bloodType (String), allergies (String), socialSecurityNumber (String)
                            Patient p = new Patient(
                                    createdUser.getId(),
                                    new Date(), // Date du jour par défaut
                                    "Non spécifié",
                                    "Inconnu",
                                    "Aucune",
                                    "000-00-0000"
                            );
                            db.patientDao().insert(p);
                        }
                    }

                    handler.post(() -> {
                        Toast.makeText(this, "Compte " + role + " créé avec succès !", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    handler.post(() -> Toast.makeText(this, "Erreur d'inscription", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
}