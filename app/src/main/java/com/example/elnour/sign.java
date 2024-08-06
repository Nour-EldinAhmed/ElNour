package com.example.elnour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.elnour.databinding.ActivitySignInBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class sign extends AppCompatActivity {

    ActivitySignInBinding signInBinding;
    FirebaseAuth auth;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://elnour-61667-default-rtdb.firebaseio.com/");
    static String phone;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private static final String KEY_PREF = "prefs";
    private static final String KEY_remmber = "remmber";
    static final String KEY_email = "email";
    static final String KEY_PASSWORD = "password";

    static String subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signInBinding.getRoot());
        Paper.init(this);
        auth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
        editor = prefs.edit();

        signInBinding.btnRegisiter.setOnClickListener(v -> {

            String edit_phone = signInBinding.editLoginphone.getText().toString();
            String edit_password = signInBinding.editLoginpassword.getText().toString();
            phone = edit_phone;

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phoneNumber", edit_phone);

            editor.apply();

            if (edit_phone.isEmpty() || edit_password.isEmpty()) {
                Toast.makeText(this, "Please Fill Empty Field", Toast.LENGTH_SHORT).show();
            } else {
                reference.child("teacher").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(edit_phone)) {
                            final String password = snapshot.child(edit_phone).child("password").getValue(String.class);
                            final String subject = snapshot.child(edit_phone).child("subject").getValue(String.class);
                            if (password.equals(edit_password)) {
                                Paper.book().write("sub", subject);  // Save the subject using Paper
                                Toast.makeText(sign.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), Choices_Name.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(sign.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(sign.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

}
