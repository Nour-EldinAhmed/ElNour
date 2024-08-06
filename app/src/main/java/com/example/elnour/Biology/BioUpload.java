package com.example.elnour.Biology;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.elnour.R;
import com.example.elnour.Video;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BioUpload extends AppCompatActivity {

    private EditText videoNameEditText, priceEditText;
    private Spinner subjectSpinner, gradeYearSpinner;
    private Button uploadButton, selectVideoButton;
    private VideoView videoView;
    private Uri videoUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private TextView progressText;
    private RelativeLayout progressLayout;
    private FirebaseFirestore firestore;

    private NotificationManagerCompat notificationManager;
    private static final String CHANNEL_ID = "upload_channel";
    private int notificationId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        videoNameEditText = findViewById(R.id.et_video_name);
        priceEditText = findViewById(R.id.et_video_price);
        subjectSpinner = findViewById(R.id.spinner_category);
        gradeYearSpinner = findViewById(R.id.spinner_grade);
        uploadButton = findViewById(R.id.btn_upload_video);
        selectVideoButton = findViewById(R.id.btn_choose_video);
        videoView = findViewById(R.id.video_view);
        progressBar = findViewById(R.id.progressbar);
        progressText = findViewById(R.id.progress_text);
        progressLayout = findViewById(R.id.progress_layout);

        // إعداد الـ Spinner للتخصص
        ArrayAdapter<CharSequence> subjectAdapter = ArrayAdapter.createFromResource(this,
                R.array.subjects, android.R.layout.simple_spinner_item);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(subjectAdapter);

        // إعداد الـ Spinner للسنة الدراسية
        ArrayAdapter<CharSequence> gradeYearAdapter = ArrayAdapter.createFromResource(this,
                R.array.classs, android.R.layout.simple_spinner_item);
        gradeYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeYearSpinner.setAdapter(gradeYearAdapter);

        storageReference = FirebaseStorage.getInstance().getReference("videos/Biology");
        databaseReference = FirebaseDatabase.getInstance().getReference("videos/Biology");
        firestore = FirebaseFirestore.getInstance();
        notificationManager = NotificationManagerCompat.from(this);

        createNotificationChannel();

        selectVideoButton.setOnClickListener(v -> selectVideo());
        uploadButton.setOnClickListener(v -> uploadVideo());
    }

    private void selectVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(videoUri);
            videoView.start();
        }
    }

    private void uploadVideo() {
        if (videoUri != null) {
            String videoName = videoNameEditText.getText().toString().trim();
            String price = priceEditText.getText().toString().trim();
            String subject = subjectSpinner.getSelectedItem().toString();
            String gradeYear = gradeYearSpinner.getSelectedItem().toString();

            if (videoName.isEmpty() || price.isEmpty() || subject.isEmpty() || gradeYear.isEmpty()) {
                Toast.makeText(this, "من فضلك املأ جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }

            progressLayout.setVisibility(View.VISIBLE);

            StorageReference videoRef = storageReference.child(System.currentTimeMillis() + ".mp4");
            videoRef.putFile(videoUri)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);

                            progressText.setText((int) progress + "%");
                            uploadButton.setVisibility(View.GONE);
                            selectVideoButton.setVisibility(View.GONE);

                            showUploadingNotification((int) progress);
                        }
                    })
                    .addOnSuccessListener(taskSnapshot -> videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Video video = new Video(videoName, uri.toString(), price, subject, gradeYear);
                        String uploadId = UUID.randomUUID().toString(); // توليد كود تحقق فريد
                        databaseReference.child(uploadId).setValue(video);

                        // إضافة الفيديو إلى Firestore داخل مجموعة paymentCodes
                        Map<String, Object> videoData = new HashMap<>();
                        videoData.put("videoUrl", uri.toString());
                        videoData.put("code", uploadId); // استخدم الكود الفريد كرمز التحقق
                        firestore.collection("paymentCodes").document(uploadId).set(videoData);

                        progressLayout.setVisibility(View.GONE);
                        showSuccessNotification();

                        Toast.makeText(BioUpload.this, "تم رفع الفيديو", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), BiologyVideosActivity.class);
                        startActivity(intent);
                    }))
                    .addOnFailureListener(e -> {
                        progressLayout.setVisibility(View.GONE);
                        showFailureNotification();
                        Toast.makeText(BioUpload.this, "فشل رفع الفيديو", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "لم يتم اختيار أي فيديو", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUploadingNotification(int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("جارٍ رفع الفيديو")
                .setContentText("جاري الرفع: " + progress + "%")
                .setSmallIcon(R.drawable.elnour)
                .setOngoing(true)
                .setProgress(100, progress, false)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        notificationManager.notify(notificationId, builder.build());
    }

    private void showSuccessNotification() {
        Intent intent = new Intent(this, BiologyVideosActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("رفع الفيديو")
                .setContentText("تم رفع الفيديو بنجاح")
                .setSmallIcon(R.drawable.elnour)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setProgress(0, 0, false)
                .setOngoing(false);

        notificationManager.notify(notificationId, builder.build());
    }

    private void showFailureNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("فشل رفع الفيديو")
                .setContentText("حدث خطأ أثناء رفع الفيديو")
                .setSmallIcon(R.drawable.elnour)
                .setAutoCancel(true)
                .setOngoing(false);

        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Upload Channel";
            String description = "Channel for video upload notifications";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
