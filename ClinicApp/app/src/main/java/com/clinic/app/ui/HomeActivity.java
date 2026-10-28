package com.clinic.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.clinic.app.R;
import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout btnConsultation, btnUrgence, btnSuivi;
    private MaterialCardView cardLogo;
    private Button btnBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- 1. INITIALIZE VIEWS ---
        cardLogo = findViewById(R.id.card_logo);
        btnBook = findViewById(R.id.btnBook);
        btnConsultation = findViewById(R.id.btn_consultation);
        btnUrgence = findViewById(R.id.btn_urgence);
        btnSuivi = findViewById(R.id.btn_suivi);

        // --- 2. SET ONCLICK LISTENERS ---

        // Clicking the logo navigates to the generic login screen
        if (cardLogo != null) {
            cardLogo.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                startActivity(intent);
            });
        }

        // Create an Intent for the booking flow
        Intent bookingFlowIntent = new Intent(HomeActivity.this, SignInActivity.class);
        bookingFlowIntent.putExtra("POST_LOGIN_ACTION", "GO_TO_BOOKING");

        // Consultation, Suivi, and the main Book button all start the booking flow
        if (btnConsultation != null) {
            btnConsultation.setOnClickListener(v -> startActivity(bookingFlowIntent));
        }

        if (btnSuivi != null) {
            btnSuivi.setOnClickListener(v -> startActivity(bookingFlowIntent));
        }

        if (btnBook != null) {
            btnBook.setOnClickListener(v -> startActivity(bookingFlowIntent));
        }

        // The Urgence button will have a different flow (e.g., call a number)
        if (btnUrgence != null) {
            btnUrgence.setOnClickListener(v -> {
                // TODO: Implement emergency action
            });
        }
    }
}
