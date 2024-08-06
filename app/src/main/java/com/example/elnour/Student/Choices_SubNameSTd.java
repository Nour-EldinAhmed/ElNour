package com.example.elnour.Student;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elnour.Choices;
import com.example.elnour.R;

public class Choices_SubNameSTd extends AppCompatActivity {
    private String phoneNumber, subject;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choices_sub_name_std);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(getApplicationContext(), STDSignIn.class);
            startActivity(intent);
            finish();
        });

        // Retrieve phone number and subject from SharedPreferences
        phoneNumber = sharedPreferences.getString("phoneNumber", "");

        // Try to get the subject from the intent, fallback to SharedPreferences
        subject = getIntent().getStringExtra("subject");
        if (subject == null) {
            subject = sharedPreferences.getString("subject", "");
        }

        // Check if user is logged in
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(this, STDSignIn.class);
            startActivity(intent);
            finish();
            return;
        }

        // Set up button click listeners
        findViewById(R.id.layarastd).setOnClickListener(v -> navigateToAppropriateActivity("Arabic"));
        findViewById(R.id.laybiostd).setOnClickListener(v -> navigateToAppropriateActivity("Biology"));
        findViewById(R.id.laygeostd).setOnClickListener(v -> navigateToAppropriateActivity("Geology"));
    }

    // Method to navigate to the appropriate activity based on subscription status
    private void navigateToAppropriateActivity(String subject) {
        Intent intent = new Intent(this, Choices.class);
        intent.putExtra("subject", subject); // Add the subject to the intent
        startActivity(intent);
    }
}
