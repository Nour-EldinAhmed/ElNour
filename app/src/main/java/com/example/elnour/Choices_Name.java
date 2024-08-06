package com.example.elnour;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elnour.Arabic.ArabicVideosActivity;
import com.example.elnour.Biology.BiologyVideosActivity;
import com.example.elnour.Geology.GeologyVideosActivity;
import com.example.elnour.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Choices_Name extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String phoneNumber, subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choices_name);

        // Retrieve phone number and subject from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        phoneNumber = sharedPreferences.getString("phoneNumber", "");
        subject = sharedPreferences.getString("subject", "");

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("teacher").child(phoneNumber).child(subject);

        // Set up button click listeners
        switch (subject) {
            case "عربي":
                findViewById(R.id.layara).setOnClickListener(v -> navigateToSubjectActivity("عربي"));
                break;
            case "احياء":
                findViewById(R.id.laybio).setOnClickListener(v -> navigateToSubjectActivity("احياء"));
                break;
            case "جيولوجيا":
                findViewById(R.id.laygeo).setOnClickListener(v -> navigateToSubjectActivity("جيولوجيا"));
                break;
            default:
                Toast.makeText(this, "Unknown subject: " + subject, Toast.LENGTH_SHORT).show();
        }
    }

    // Method to navigate to the corresponding activity
    private void navigateToSubjectActivity(String subject) {
        Intent intent;
        switch (subject) {
            case "احياء":
                intent = new Intent(this, BiologyVideosActivity.class);
                break;
            case "عربي":
                intent = new Intent(this, ArabicVideosActivity.class);
                break;
            case "جيولوجيا":
                intent = new Intent(this, GeologyVideosActivity.class);
                break;

            default:
                Toast.makeText(this, "Unknown subject: " + subject, Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
    }
}
