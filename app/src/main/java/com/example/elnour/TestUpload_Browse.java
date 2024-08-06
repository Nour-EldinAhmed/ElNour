package com.example.elnour;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class TestUpload_Browse extends AppCompatActivity {

    EditText edit, edit_price;
    TextView txt_name;
    static String name;
    static String namegio = "gio";

    DatabaseReference databaseReference;
    StorageReference storageReference;
    LinearProgressIndicator progressIndicator;
    Spinner sp;
    Uri video;
    MaterialButton selectVideo, uploadVideo;
    ImageView imageView;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    uploadVideo.setEnabled(true);
                    video = result.getData().getData();
                    Glide.with(TestUpload_Browse.this).load(video).into(imageView);
                }
            } else {
                Toast.makeText(TestUpload_Browse.this, "Please select a video", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload_browse);
        FirebaseApp.initializeApp(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("videos");
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sp = findViewById(R.id.sp_sss);
        imageView = findViewById(R.id.imageView);
        progressIndicator = findViewById(R.id.process);
        selectVideo = findViewById(R.id.selectVideo);
        uploadVideo = findViewById(R.id.uploadVideo);
        edit = findViewById(R.id.edit_v);
        edit_price = findViewById(R.id.edit_price);
        // txt_name=findViewById(R.id.txt_n);
//        name=txt_name.getText().toString();
        name=sp.getSelectedItem().toString();
        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                activityResultLauncher.launch(intent);


            }
        });

        uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadVideo(video);
            }
        });
    }

    private void uploadVideo(Uri uri) {
        StorageReference reference = storageReference.child("videos/" + UUID.randomUUID().toString());
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FileModel fileModel = new FileModel(edit.getText().toString(), uri.toString(), Integer.parseInt(edit_price.getText().toString()),"",name);
                databaseReference.child(databaseReference.push().getKey()).setValue(fileModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(TestUpload_Browse.this, "Text Sueccful", Toast.LENGTH_SHORT).show();

                            }
                        });
                uploadVideo.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplicationContext(), CoursesActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TestUpload_Browse.this, "Failed to upload video", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressIndicator.setVisibility(View.VISIBLE);
                progressIndicator.setMax(Math.toIntExact(snapshot.getTotalByteCount()));
                progressIndicator.setProgress(Math.toIntExact(snapshot.getBytesTransferred()));
            }
        });
    }
}