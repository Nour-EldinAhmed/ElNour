package com.example.elnour;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.elnour.Geology.GeologyVideosActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UploadVideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;

    private ImageView ivVideoPreview;
    private Button btnSelectVideo, btnUploadVideo;
    private EditText etVideoName, etVideoPrice;
    private Spinner spVideoCategory;
    private ProgressBar progressBar;

    private Uri videoUri;
    private String selectedCategory;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        ivVideoPreview = findViewById(R.id.iv_video_preview);
        btnSelectVideo = findViewById(R.id.btn_select_video);
        btnUploadVideo = findViewById(R.id.btn_upload_video);
        etVideoName = findViewById(R.id.et_video_name);
        etVideoPrice = findViewById(R.id.et_video_price);
        spVideoCategory = findViewById(R.id.sp_video_category);
        progressBar = findViewById(R.id.progress_bar);

        storageReference = FirebaseStorage.getInstance().getReference("videos/Geology");
        databaseReference = FirebaseDatabase.getInstance().getReference("videos/Geology");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.subjects, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVideoCategory.setAdapter(adapter);
        spVideoCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "General";
            }
        });

        btnSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(UploadVideoActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            ivVideoPreview.setImageURI(videoUri);
        }
    }

    private String getFileExtension(Uri uri) {
        return getContentResolver().getType(uri).split("/")[1];
    }

    private void uploadFile() {
        if (videoUri != null) {
            progressBar.setVisibility(View.VISIBLE);

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(videoUri));
            uploadTask = fileReference.putFile(videoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String videoUrl = uri.toString();

                                    String videoName = etVideoName.getText().toString().trim();
                                    String videoPrice = etVideoPrice.getText().toString().trim();

                                    Map<String, String> videoData = new HashMap<>();
                                    videoData.put("name", videoName);
                                    videoData.put("price", videoPrice);
                                    videoData.put("url", videoUrl);
                                    videoData.put("category", selectedCategory);

                                    String uploadId = databaseReference.child(selectedCategory).push().getKey();
                                    databaseReference.child(selectedCategory).child(uploadId).setValue(videoData);

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(UploadVideoActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(UploadVideoActivity.this, GeologyVideosActivity.class);
                                    intent.putExtra("category", selectedCategory);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(UploadVideoActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
