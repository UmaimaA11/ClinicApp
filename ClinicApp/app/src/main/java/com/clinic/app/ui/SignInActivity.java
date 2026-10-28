package com.clinic.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.clinic.app.R;
import com.clinic.app.data.entities.User;
import com.clinic.app.data.entities.UserRole;
import com.clinic.app.domain.usecase.authentication.SignInUseCase;
import com.clinic.app.util.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignInActivity extends AppCompatActivity {

    private EditText emailEntry, passwordEntry;
    private Button loginButton;
    private TextView signupText;

    private SignInUseCase signInUseCase;
    // Correction : Ajout de l'initialisation de l'executor qui manquait
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInUseCase = new SignInUseCase(getApplication());
        emailEntry = findViewById(R.id.username);
        passwordEntry = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);

        loginButton.setOnClickListener(v -> {
            String email = emailEntry.getText().toString().trim().toLowerCase();
            String password = passwordEntry.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs !", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. GESTION ADMIN (Direct)
            if (email.equals("admin@clinic.com") && password.equals("admin123")) {
                // On crée un objet User temporaire pour l'admin
                User admin = new User("Admin", email, 0, password, UserRole.ADMIN);
                admin.setId(0); // L'ID 0 est souvent réservé à l'admin
                handleSuccessfulLogin(admin);
                return;
            }

            // 2. GESTION DOCTEUR / PATIENT (Via BDD)
            executor.execute(() -> {
                User user = signInUseCase.execute(email, password);
                handler.post(() -> {
                    if (user != null) {
                        handleSuccessfulLogin(user);
                    } else {
                        Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        signupText.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void handleSuccessfulLogin(User user) {
        try {
            // Sauvegarde de la session
            SessionManager session = new SessionManager(getApplicationContext());
            // On s'assure que user.getId() renvoie un long ou int valide
            session.createLoginSession((int)user.getId(), user.getName(), user.getEmail());

            Toast.makeText(this, "Bienvenue " + user.getName(), Toast.LENGTH_SHORT).show();

            Intent intent;
            if (user.getRole() == UserRole.ADMIN) {
                intent = new Intent(this, AdminDashboardActivity.class);
            } else if (user.getRole() == UserRole.DOCTOR) {
                intent = new Intent(this, MedDashboardActivity.class);
            } else {
                // Pour le patient (Malak)
                intent = new Intent(this, com.clinic.app.malak.DashboardActivity.class);
            }

            // Passer l'ID à l'activité suivante pour éviter le message "Session expirée"
            intent.putExtra("USER_ID", user.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la création de session", Toast.LENGTH_SHORT).show();
        }
    }
}