package com.example.elnour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class SiginUp extends AppCompatActivity {

    private EditText editTextFullName, editTextPhoneNumber,
            editTextPassword, editTextCity, editTextProvince,edit_email;
    private Spinner sp_subject,sp_class;
    private Button buttonRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String subject;
    static String sub;
    Button btn_sign;
    TextView txtlink;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://elnour-61667-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin_up);

        FirebaseApp.initializeApp(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btn_sign=findViewById(R.id.btn_signin_signup);
        sp_subject=findViewById(R.id.edit_siginupsubject);
        editTextFullName = findViewById(R.id.edit_name_siginup);
        sp_class = findViewById(R.id.edit_classsiginup);
        editTextPhoneNumber = findViewById(R.id.edit_phoneregisiter);
        editTextPassword = findViewById(R.id.edit_signuppassword);
        editTextCity = findViewById(R.id.edit_citysigiup);
        editTextProvince = findViewById(R.id.edit_mohafzasigiup);
        buttonRegister = findViewById(R.id.btn_siginup);
        txtlink=findViewById(R.id.txt_link);
        edit_email=findViewById(R.id.edit_email);
/*
        txtlink.setOnClickListener(view -> {
            Intent intent=new Intent(getApplicationContext() , SignIn.class);
            startActivity(intent);
        });*/

            buttonRegister.setOnClickListener(view -> {

                String email = editTextPhoneNumber.getText().toString();
                String password = editTextPassword.getText().toString().trim();
                String fullName = editTextFullName.getText().toString().trim();
                String grade = sp_class.getSelectedItem().toString().trim();
                String city = editTextCity.getText().toString().trim();
                String province = editTextProvince.getText().toString().trim();
                        subject=sp_subject.getSelectedItem().toString();
               String mail=edit_email.getText().toString().trim();

                // Save phone number to SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phoneNumber", editTextPhoneNumber.getText().toString());
                editor.putString("subject", subject);

                editor.apply();

                if (fullName.isEmpty() || password.isEmpty() || grade.isEmpty() || city.isEmpty() || province.isEmpty()||subject.isEmpty()||mail.isEmpty()) {
                    Toast.makeText(this, "Please fill Empty filed", Toast.LENGTH_SHORT).show();
                } else {


                    ref.child("teacher").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(email)) {
                                Toast.makeText(SiginUp.this, "Phone is Aleardy Regisitered", Toast.LENGTH_SHORT).show();
                            } else {
                                ref.child("teacher").child(email).child("password").setValue(password);
                                ref.child("teacher").child(email).child("fullname").setValue(fullName);
                                ref.child("teacher").child(email).child("grade").setValue(grade);
                                ref.child("teacher").child(email).child("city").setValue(city);
                                ref.child("teacher").child(email).child("province").setValue(province);
                                ref.child("teacher").child(email).child("subject").setValue(subject);
                                ref.child("teacher").child(email).child("Email").setValue(mail);
                                sub=subject;

                                Toast.makeText(getApplicationContext(), "Regisitered Succfully", Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(getApplicationContext(), Choices_Name.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            btn_sign.setOnClickListener(view -> {
                Intent intent=new Intent(this, SignIn.class);
                startActivity(intent);
            });

        }


    }
