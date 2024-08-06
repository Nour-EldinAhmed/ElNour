package com.example.elnour;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elnour.Student.Geology.PurchasedVideosActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SubscriptionActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "PaymentPrefs";
    private static final String KEY_PURCHASED_VIDEOS = "purchased_videos";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);  // تأكد من تعيين ملف XML

        Intent intent = getIntent();
        ArrayList<String> videoUrls = intent.getStringArrayListExtra("videoUrls");
        String subscriptionType = intent.getStringExtra("subscriptionType");

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Set<String> purchasedVideos = sharedPreferences.getStringSet(KEY_PURCHASED_VIDEOS, new HashSet<>());

        if (purchasedVideos.containsAll(videoUrls)) {
            // إذا كانت جميع الفيديوهات مشتراة، اسمح للمستخدم بمشاهدتها
            Intent videoPlayerIntent = new Intent(this, VideoPlayerActivity.class);
            videoPlayerIntent.putStringArrayListExtra("videoUrls", videoUrls);
            startActivity(videoPlayerIntent);
        } else {
            Button monthlySubscriptionButton = findViewById(R.id.btn_monthly_subscription);
            monthlySubscriptionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSubscriptionOptionsDialog(videoUrls);
                }
            });
        }
    }

    private void showSubscriptionOptionsDialog(ArrayList<String> videoUrls) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Subscription Options")
                .setMessage("Do you want to go to your subscription or browse more courses?")
                .setPositiveButton("Go to My Subscription", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showVerificationDialog(videoUrls);
                    }
                })
                .setNegativeButton("Browse More Courses", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Dismiss dialog
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void showVerificationDialog(ArrayList<String> videoUrls) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verification");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String enteredCode = input.getText().toString().trim();
                if (!enteredCode.isEmpty()) {
                    verifyPaymentCode(enteredCode, videoUrls);
                } else {
                    Toast.makeText(SubscriptionActivity.this, "Please enter the verification code.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    private void verifyPaymentCode(String enteredCode, ArrayList<String> videoUrls) {
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
                                purchasedVideos.addAll(videoUrls);
                                editor.putStringSet(KEY_PURCHASED_VIDEOS, purchasedVideos);
                                editor.apply();

                                Intent intent = new Intent(SubscriptionActivity.this, PurchasedVideosActivity.class);
                                intent.putStringArrayListExtra("videoUrls", videoUrls);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SubscriptionActivity.this, "Invalid verification code.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SubscriptionActivity.this, "Verification code does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SubscriptionActivity.this, "Failed to verify code.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
