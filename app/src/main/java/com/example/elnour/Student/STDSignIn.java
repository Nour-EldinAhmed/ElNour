package com.example.elnour.Student;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elnour.R;
import com.example.elnour.SupportActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class STDSignIn extends AppCompatActivity {

    private EditText phoneEditText, passwordEditText;
    private Spinner subjectSpinner;
    private Button signInButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stdsign_in);

        phoneEditText = findViewById(R.id.edit_loginphonestd);
        passwordEditText = findViewById(R.id.edit_loginpasswordstd);
       // subjectSpinner = findViewById(R.id.sp_subjectsstd);
        signInButton = findViewById(R.id.btn_regisiterstd);

        databaseReference = FirebaseDatabase.getInstance().getReference("students");

        findViewById(R.id.txt_linkstd).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), STDSignUp.class);
            startActivity(intent);
        });

        findViewById(R.id.button_support).setOnClickListener(V -> {
            Intent intent = new Intent(getApplicationContext(), SupportActivity.class);
            startActivity(intent);
        });

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            // User is logged in, redirect to the specialization page
            Intent intent = new Intent(getApplicationContext(), Choices_SubNameSTd.class);
            startActivity(intent);
            finish(); // Finish this activity so the user can't return to it
            return;
        }

        signInButton.setOnClickListener(v -> loginStudent());
    }

    private void loginStudent() {
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
//        String subject = subjectSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    STDSignUp.Student student = dataSnapshot.getValue(STDSignUp.Student.class);
                    if (student != null && student.phone.equals(phone) && student.password.equals(password)) {
                        found = true;
                        // Save user details and login state
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phoneNumber", phone);
                        editor.putBoolean("isLoggedIn", true); // Mark as logged in
                        editor.apply();
                        break;
                    }
                }
                if (found) {
                    Toast.makeText(STDSignIn.this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Choices_SubNameSTd.class);
                    startActivity(intent);
                    finish(); // Finish this activity so the user can't return to it
                } else {
                    Toast.makeText(STDSignIn.this, "فشل تسجيل الدخول، تأكد من البيانات", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(STDSignIn.this, "حدث خطأ، حاول مرة أخرى", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
