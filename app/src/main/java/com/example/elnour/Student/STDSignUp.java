package com.example.elnour.Student;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elnour.R;
import com.example.elnour.SupportActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class STDSignUp extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, passwordEditText, provinceEditText, cityEditText;
    private Spinner subjectSpinner,classEditText;
    private Button signUpButton;
    private ImageButton btn_sup;

    TextView txt_link;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stdsign_up);
        btn_sup=findViewById(R.id.button_support);
        nameEditText = findViewById(R.id.edit_name_siginup_std);
        classEditText = findViewById(R.id.edit_classsiginup_std);
        phoneEditText = findViewById(R.id.edit_phoneregisiter_std);
        passwordEditText = findViewById(R.id.edit_signuppassword_std);
        provinceEditText = findViewById(R.id.edit_mohafzasigiup_std);
        cityEditText = findViewById(R.id.edit_citysigiup_std);
        subjectSpinner = findViewById(R.id.edit_siginupsubject_std);
        signUpButton = findViewById(R.id.btn_siginup);
        txt_link=findViewById(R.id.txt_link);
        btn_sup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), SupportActivity.class);
                startActivity(intent);
            }
        });
        // تغيير اسم قاعدة البيانات إلى students
        databaseReference = FirebaseDatabase.getInstance().getReference("students");

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerStudent();
            }
        });

        txt_link.setOnClickListener(V->{
            Intent intent=new Intent(getApplicationContext(), STDSignIn.class);
            startActivity(intent);
        });
    }

    private void registerStudent() {
        String name = nameEditText.getText().toString().trim();
        String className = classEditText.getSelectedItem().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String province = provinceEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String subject = subjectSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(className) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(province) || TextUtils.isEmpty(city)) {
            Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        String studentId = databaseReference.push().getKey();
        Student student = new Student(name, className, phone, password, subject, province, city);

        if (studentId != null) {
            databaseReference.child(studentId).setValue(student).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(STDSignUp.this, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(getApplicationContext(),STDSignIn.class);
                    startActivity(intent);
                    clearFields();
                } else {
                    Toast.makeText(STDSignUp.this, "فشل في التسجيل، حاول مرة أخرى", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void clearFields() {
        nameEditText.setText("");
        classEditText.setSelection(0);
        phoneEditText.setText("");
        passwordEditText.setText("");
        provinceEditText.setText("");
        cityEditText.setText("");
        subjectSpinner.setSelection(0);
    }

    public static class Student {
        public String name;
        public String className;
        public String phone;
        public String password;
        public String subject;
        public String province;
        public String city;

        public Student() {
            // Default constructor required for calls to DataSnapshot.getValue(Student.class)
        }

        public Student(String name, String className, String phone, String password, String subject, String province, String city) {
            this.name = name;
            this.className = className;
            this.phone = phone;
            this.password = password;
            this.subject = subject;
            this.province = province;
            this.city = city;
        }
    }
}
