package com.example.elnour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentActivity extends AppCompatActivity {

    private Button buttonSendWhatsApp;
    private EditText editTextVerificationCode;
    private Button buttonVerify;
    private FirebaseFirestore db;
    private String videoName;
    private String videoPrice;
    private String videoSubject;
    private String videoGradeYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        buttonSendWhatsApp = findViewById(R.id.buttonSendWhatsApp);
        editTextVerificationCode = findViewById(R.id.editTextVerificationCode);
        buttonVerify = findViewById(R.id.buttonVerify);
        videoName = getIntent().getStringExtra("videoName");
        videoPrice = getIntent().getStringExtra("videoPrice");
        videoSubject = getIntent().getStringExtra("videoSubject");
        videoGradeYear = getIntent().getStringExtra("videoGradeYear");

        db = FirebaseFirestore.getInstance();

        buttonSendWhatsApp.setOnClickListener(v -> {
            // صياغة الرسالة
            String message = "اسم الفيديو: " + videoName + "\n"
                    + "السعر: " + videoPrice + " EGP\n"
                    + "التخصص: " + videoSubject + "\n"
                    + "السنة الدراسية: " + videoGradeYear;

            // رابط WhatsApp مع الرسالة
            String url = "https://wa.me/201040581954?text=" + Uri.encode(message);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);

            // افتراض أن المستخدم أرسل الرسالة وعاد إلى التطبيق
            editTextVerificationCode.setVisibility(View.VISIBLE);
            buttonVerify.setVisibility(View.VISIBLE);
        });


        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPaymentCode();
            }
        });
    }

    private void sendWhatsAppMessage() {
        Uri uri = Uri.parse("https://wa.me/201040581954");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
        sendIntent.setPackage("com.whatsapp");
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
            editTextVerificationCode.setVisibility(View.VISIBLE);
            buttonVerify.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(PaymentActivity.this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyPaymentCode() {
        String enteredCode = editTextVerificationCode.getText().toString().trim();
        if (enteredCode.isEmpty()) {
            Toast.makeText(PaymentActivity.this, "Please enter the verification code.", Toast.LENGTH_SHORT).show();
            return;
        }

        String videoUrl = getIntent().getStringExtra("videoUrl"); // احصل على videoUrl هنا

        db.collection("paymentCodes").document(enteredCode).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                boolean isCodeValid = document.contains("code") && document.getString("code").equals(enteredCode);
                                if (isCodeValid) {
                                    db.collection("paymentCodes").document(enteredCode)
                                            .update("videos", FieldValue.arrayUnion(videoUrl))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent intent = new Intent(PaymentActivity.this, VideoPlayerActivity.class);
                                                        intent.putExtra("videoUrl", videoUrl); // تمرير videoUrl
                                                        startActivity(intent);
                                                    } else {
                                                        Toast.makeText(PaymentActivity.this, "Failed to update video list.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(PaymentActivity.this, "Invalid verification code.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(PaymentActivity.this, "Verification code does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PaymentActivity.this, "Failed to verify code.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
