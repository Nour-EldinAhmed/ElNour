package com.example.elnour.Student.Geology;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elnour.R;
import com.example.elnour.Video;
import com.example.elnour.VideoAdapter;
import com.example.elnour.VideoPlayerActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchasedVideosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private List<Video> videoList;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_videos);

        recyclerView = findViewById(R.id.recycler_view_purchased_videos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(videoList, this);
        recyclerView.setAdapter(videoAdapter);

        verificationCode = getIntent().getStringExtra("verificationCode");

        if (verificationCode != null && !verificationCode.isEmpty()) {
            loadPurchasedVideos();
        } else {
            Toast.makeText(this, "لا يوجد كود تحقق", Toast.LENGTH_SHORT).show();
        }

        videoAdapter.setOnItemClickListener(video -> {
            Intent intent = new Intent(PurchasedVideosActivity.this, VideoPlayerActivity.class);
            intent.putExtra("videoUrl", video.getVideoUrl());
            startActivity(intent);
        });
    }

    private void loadPurchasedVideos() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("videos")
                .document(verificationCode) // استخدام كود التحقق كـ Document ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<Map<String, Object>> videoDetailsList = (List<Map<String, Object>>) document.get("videos");
                            videoList.clear();
                            if (videoDetailsList != null) {
                                for (Map<String, Object> videoDetails : videoDetailsList) {
                                    String videoName = (String) videoDetails.get("videoName");
                                    String videoUrl = (String) videoDetails.get("videoUrl");
                                    String price = (String) videoDetails.get("price");
                                    String subject = (String) videoDetails.get("subject");
                                    String gradeYear = (String) videoDetails.get("gradeYear");

                                    Video video = new Video(videoName, videoUrl, price, subject, gradeYear);
                                    videoList.add(video);
                                }
                            }
                            if (videoList.isEmpty()) {
                                Toast.makeText(PurchasedVideosActivity.this, "لا توجد فيديوهات مطابقة", Toast.LENGTH_SHORT).show();
                            }
                            videoAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(PurchasedVideosActivity.this, "لا توجد وثيقة بهذا الكود", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PurchasedVideosActivity.this, "فشل في تحميل الفيديوهات", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
