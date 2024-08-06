package com.example.elnour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.elnour.databinding.ActivitySignInBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    ActivitySignInBinding signInBinding;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://elnour-61667-default-rtdb.firebaseio.com/");
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private static final String KEY_PREF = "prefs";
    private static final String KEY_remmber = "remmber";
    static final String KEY_email = "email";
    static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signInBinding.getRoot());
        prefs = getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
        editor = prefs.edit();

        // Check if the user is already logged in
        if (prefs.getBoolean("isLoggedIn", false)) {
            // Redirect to Choices_Name activity
            Intent intent = new Intent(getApplicationContext(), Choices_Name.class);
            startActivity(intent);
            finish(); // Finish this activity so the user can't return to it
            return;
        }

        signInBinding.btnRegisiter.setOnClickListener(v -> {
            String edit_phone = signInBinding.editLoginphone.getText().toString();
            String edit_password = signInBinding.editLoginpassword.getText().toString();
            String subjects = signInBinding.spSubjects.getSelectedItem().toString();

            if (edit_phone.isEmpty() || edit_password.isEmpty()) {
                Toast.makeText(this, "Please Fill Empty Field", Toast.LENGTH_SHORT).show();
            } else {
                reference.child("teacher").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(edit_phone)) {
                            final String password = snapshot.child(edit_phone).child("password").getValue(String.class);
                            final String subject = snapshot.child(edit_phone).child("subject").getValue(String.class);

                            if (password.equals(edit_password) && subject.equals(subjects)) {
                                // Save user details and login state
                                editor.putString("phoneNumber", edit_phone);
                                editor.putString("subject", subject);
                                editor.putBoolean("isLoggedIn", true); // Mark as logged in
                                editor.apply();

                                Toast.makeText(SignIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Choices_Name.class);
                                startActivity(intent);
                                finish(); // Finish this activity so the user can't return to it
                            } else {
                                Toast.makeText(SignIn.this, "Wrong Password or Subject", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignIn.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SignIn.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
