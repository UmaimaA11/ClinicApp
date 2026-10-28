package com.clinic.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.clinic.app.R;
import com.google.android.material.card.MaterialCardView;

public class AdminDashboardActivity extends AppCompatActivity {

    private MaterialCardView btnManageUsers, btnManageAppointments, btnManageTimeSlots;
    private ImageButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnManageUsers = findViewById(R.id.btn_manage_users);
        btnManageAppointments = findViewById(R.id.btn_manage_appointments);
        btnManageTimeSlots = findViewById(R.id.btn_manage_timeslots);
        btnLogout = findViewById(R.id.btn_logout);

        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageUsersActivity.class);
            startActivity(intent);
        });

        btnManageAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageAppointmentsActivity.class);
            startActivity(intent);
        });

        btnManageTimeSlots.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageTimeSlotsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
        });
    }
}
