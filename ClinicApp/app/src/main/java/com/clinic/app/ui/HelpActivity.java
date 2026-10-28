package com.clinic.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.clinic.app.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        ImageView btnBack = findViewById(R.id.btn_back);
        LinearLayout btnCall = findViewById(R.id.btn_call_support);
        LinearLayout btnEmail = findViewById(R.id.btn_email_support);

        btnBack.setOnClickListener(v -> finish());

        // Action pour appeler directement le support
        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:0123456789"));
            startActivity(intent);
        });

        // Action pour envoyer un email au support
        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@clinicapp.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Assistance Docteur ID: " + getIntent().getIntExtra("USER_ID", 0));
            startActivity(intent);
        });
    }
}