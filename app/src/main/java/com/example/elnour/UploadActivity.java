package com.example.elnour;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadActivity extends AppCompatActivity {

    private EditText videoNameEditText, priceEditText;
    private Spinner subjectSpinner, gradeYearSpinner;
    private Button uploadButton, selectVideoButton;
    private VideoView videoView;
    private Uri videoUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

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

        storageReference = FirebaseStorage.getInstance().getReference("videos");
        databaseReference = FirebaseDatabase.getInstance().getReference("videos");

        selectVideoButton.setOnClickListener(v -> selectVideo());
        uploadButton.setOnClickListener(v -> uploadVideo());

        // إعداد ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("جاري رفع الفيديو");
        progressDialog.setMessage("من فضلك انتظر...");
        progressDialog.setCancelable(false);
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

            progressDialog.show();

            StorageReference videoRef = storageReference.child(System.currentTimeMillis() + ".mp4");
            videoRef.putFile(videoUri)
                    .addOnSuccessListener(taskSnapshot -> videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Video video = new Video(videoName, uri.toString(), price, subject, gradeYear);
                        String uploadId = databaseReference.push().getKey();
                        databaseReference.child(uploadId).setValue(video);

                        progressDialog.dismiss();
                        Toast.makeText(UploadActivity.this, "تم رفع الفيديو", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(), VideosListActivity.class);
                        startActivity(intent);
                    }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(UploadActivity.this, "فشل رفع الفيديو", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "لم يتم اختيار أي فيديو", Toast.LENGTH_SHORT).show();
        }
    }
}
