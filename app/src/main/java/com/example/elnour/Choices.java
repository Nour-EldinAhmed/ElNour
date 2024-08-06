package com.example.elnour;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elnour.Student.Arabic.ArabicActivitySTD;
import com.example.elnour.Student.Biology.BiologyActivitySTD;
import com.example.elnour.Student.Geology.GeologyActivityStd;
import com.example.elnour.Student.Geology.PurchasedVideosActivity;
import com.example.elnour.Student.Biology.PurchasedVideosActivityBio;
import com.example.elnour.Student.Arabic.PurchasedVideosActivityAra;
import com.example.elnour.databinding.ActivityChoicesBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.Set;

public class Choices extends AppCompatActivity {

    private ActivityChoicesBinding binding;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "PaymentPrefs";
    private static final String KEY_PURCHASED_VIDEOS = "purchased_videos";
    private String subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChoicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        subject = getIntent().getStringExtra("subject");

        binding.submon.setOnClickListener(v -> {
            // عرض الـ Dialog عند الضغط على اشتراك شهري
            showSubscriptionOptionsDialog();
        });

        binding.subclass.setOnClickListener(v -> {
            showVideo();
        });
    }

    private void showSubscriptionOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("خيارات الاشتراك")
                .setMessage("هل ترغب في الانتقال إلى اشتراكك أم تصفح المزيد من الدورات؟")
                .setPositiveButton("اذهب إلى اشتراكي", (dialog, id) -> showVerificationDialog())
                .setNegativeButton("تصفح المزيد من الدروس", (dialog, id) -> showVideo());

        builder.create().show();
    }

    private void showVideo() {
        Intent intent;
        switch (subject) {
            case "Biology":
                intent = new Intent(getApplicationContext(), BiologyActivitySTD.class);
                break;
            case "Geology":
                intent = new Intent(getApplicationContext(), GeologyActivityStd.class);
                break;
            case "Arabic":
                intent = new Intent(getApplicationContext(), ArabicActivitySTD.class);
                break;
            default:
                Toast.makeText(this, "Unknown subject: " + subject, Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
    }

    private void showVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ادخل كود التحقق ");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("تحقق", (dialog, id) -> {
            String enteredCode = input.getText().toString().trim();
            if (!enteredCode.isEmpty()) {
                verifyPaymentCode(enteredCode);
            } else {
                Toast.makeText(Choices.this, "برجاء ادخال كود التحقق", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("الغاء", (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    private void verifyPaymentCode(String enteredCode) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("videos").document(enteredCode).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            boolean isCodeValid = document.contains("code") && document.getString("code").equals(enteredCode);
                            if (isCodeValid) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                Set<String> purchasedVideos = sharedPreferences.getStringSet(KEY_PURCHASED_VIDEOS, new HashSet<>());
                                // إضافة الفيديوهات المشتراة
                                purchasedVideos.add("video1_url"); // استبدل بـ URL الفيديوهات الفعلي
                                purchasedVideos.add("video2_url");
                                editor.putStringSet(KEY_PURCHASED_VIDEOS, purchasedVideos);
                                editor.apply();

                                Intent intent;
                                switch (subject) {
                                    case "Biology":
                                        intent = new Intent(Choices.this, PurchasedVideosActivityBio.class);
                                        break;
                                    case "Geology":
                                        intent = new Intent(Choices.this, PurchasedVideosActivity.class);
                                        break;
                                    case "Arabic":
                                        intent = new Intent(Choices.this, PurchasedVideosActivityAra.class);
                                        break;
                                    default:
                                        Toast.makeText(Choices.this, "Unknown subject: " + subject, Toast.LENGTH_SHORT).show();
                                        return;
                                }
                                intent.putExtra("verificationCode", enteredCode); // Adding verification code to the intent
                                startActivity(intent);
                            } else {
                                Toast.makeText(Choices.this, "Invalid verification code.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Choices.this, "Verification code does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Choices.this, "Failed to verify code.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
