package com.example.elnour;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SupportActivity extends AppCompatActivity {

    private EditText emailEditText, phoneEditText, suggestionsEditText;
    private Button submitButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        emailEditText = findViewById(R.id.email_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        suggestionsEditText = findViewById(R.id.suggestions_edit_text);
        submitButton = findViewById(R.id.submit_button);

        databaseReference = FirebaseDatabase.getInstance().getReference("submissions");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }

    private void submitData() {
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String suggestions = suggestionsEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(suggestions)) {
            Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey();
        Submission submission = new Submission(email, phone, suggestions);

        if (id != null) {
            databaseReference.child(id).setValue(submission).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SupportActivity.this, "تم الإرسال بنجاح", Toast.LENGTH_SHORT).show();
                    emailEditText.setText("");
                    phoneEditText.setText("");
                    suggestionsEditText.setText("");
                } else {
                    Toast.makeText(SupportActivity.this, "فشل في الإرسال، حاول مرة أخرى", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    static class Submission {
        public String email;
        public String phone;
        public String suggestions;

        public Submission() {
            // Default constructor required for calls to DataSnapshot.getValue(Submission.class)
        }

        public Submission(String email, String phone, String suggestions) {
            this.email = email;
            this.phone = phone;
            this.suggestions = suggestions;
        }
    }
}
