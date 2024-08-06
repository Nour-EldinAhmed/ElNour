package com.example.elnour.Student.Arabic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elnour.R;
import com.example.elnour.Student.Biology.PurchasedVideosActivityBio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentMonthAra extends AppCompatActivity {

    private Button buttonSendWhatsApp;
    private EditText editTextVerificationCode;
    private Button buttonVerify;
    private FirebaseFirestore db;
    private ArrayList<String> videoUrls;
    private ArrayList<String> videoNames;
    private ArrayList<String> videoPrices;
    private ArrayList<String> videoSubjects;
    private ArrayList<String> videoGradeYears;
    private TextView textViewTotalAmount;
    private int totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_month);

        buttonSendWhatsApp = findViewById(R.id.buttonSendWhatsApp);
        editTextVerificationCode = findViewById(R.id.editTextVerificationCode);
        buttonVerify = findViewById(R.id.buttonVerify);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);

        db = FirebaseFirestore.getInstance();

        videoUrls = getIntent().getStringArrayListExtra("videoUrls");
        videoNames = getIntent().getStringArrayListExtra("videoNames");
        videoPrices = getIntent().getStringArrayListExtra("videoPrices");
        videoSubjects = getIntent().getStringArrayListExtra("videoSubjects");
        videoGradeYears = getIntent().getStringArrayListExtra("videoGradeYears");
        totalAmount = getIntent().getIntExtra("totalAmount", 0);

        textViewTotalAmount.setText("المبلغ الإجمالي: " + totalAmount + " جنيه");

        buttonSendWhatsApp.setOnClickListener(v -> {
            String message = "سعر الاشتراك الشهري : " + totalAmount + " EGP\n";

            String url = "https://wa.me/201040581954?text=" + Uri.encode(message);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);

            editTextVerificationCode.setVisibility(View.VISIBLE);
            buttonVerify.setVisibility(View.VISIBLE);
        });

        buttonVerify.setOnClickListener(v -> verifyPaymentCode());
    }

    private void verifyPaymentCode() {
        String enteredCode = editTextVerificationCode.getText().toString().trim();
        if (enteredCode.isEmpty()) {
            Toast.makeText(PaymentMonthAra.this, "Please enter the verification code.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("videos").document(enteredCode).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // تحقّق من صحة الكود
                                if (document.contains("code") && document.getString("code").equals(enteredCode)) {
                                    // تخزين بيانات الفيديوهات
                                    Map<String, Object> videoDetails = new HashMap<>();
                                    ArrayList<Map<String, Object>> videoList = new ArrayList<>();
                                    for (int i = 0; i < videoUrls.size(); i++) {
                                        Map<String, Object> video = new HashMap<>();
                                        video.put("videoName", videoNames.get(i));
                                        video.put("videoUrl", videoUrls.get(i));
                                        video.put("price", videoPrices.get(i));
                                        video.put("subject", videoSubjects.get(i));
                                        video.put("gradeYear", videoGradeYears.get(i));
                                        videoList.add(video);
                                    }
                                    videoDetails.put("videos", videoList);

                                    db.collection("videos").document(enteredCode)
                                            .set(videoDetails)
                                            .addOnSuccessListener(aVoid -> {
                                                Intent intent = new Intent(PaymentMonthAra.this, PurchasedVideosActivityAra.class);
                                                intent.putExtra("verificationCode", enteredCode);
                                                intent.putStringArrayListExtra("videoUrls", videoUrls);
                                                intent.putStringArrayListExtra("videoNames", videoNames);
                                                intent.putStringArrayListExtra("videoPrices", videoPrices);
                                                intent.putStringArrayListExtra("videoSubjects", videoSubjects);
                                                intent.putStringArrayListExtra("videoGradeYears", videoGradeYears);
                                                startActivity(intent);
                                                finish(); // إنهاء النشاط الحالي بعد الانتقال
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(PaymentMonthAra.this, "Failed to store video details.", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(PaymentMonthAra.this, "Invalid verification code.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(PaymentMonthAra.this, "Verification code does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PaymentMonthAra.this, "Failed to verify code.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
