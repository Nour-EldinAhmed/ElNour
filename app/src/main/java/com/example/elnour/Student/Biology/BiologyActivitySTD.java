package com.example.elnour.Student.Biology;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elnour.Biology.BiologyVideosActivity;
import com.example.elnour.PaymentActivity;
import com.example.elnour.PaymentMonth;
import com.example.elnour.R;
import com.example.elnour.Video;
import com.example.elnour.VideoAdapter;
import com.example.elnour.VideoPlayerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BiologyActivitySTD extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private List<Video> videoList;
    private DatabaseReference databaseReference;
    private Button subscribeButton;
    private static final int MAX_SELECTION = 8;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biology_std);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.rec_videobio);
        subscribeButton = findViewById(R.id.btn_monthly_subscriptionbio);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(videoList, this);
        recyclerView.setAdapter(videoAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("videos/Biology");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Video video = postSnapshot.getValue(Video.class);
                    videoList.add(video);
                }
                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BiologyActivitySTD.this, "Failed to load videos", Toast.LENGTH_SHORT).show();
            }
        });
/*
        videoAdapter.setOnItemClickListener(video -> {
            String videoUrl = video.getVideoUrl();
            db.collection("paymentCodes").whereArrayContains("videos", videoUrl).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                Intent intent = new Intent(GeologyActivityStd.this, VideoPlayerActivity.class);
                                intent.putExtra("videoUrl", videoUrl);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(GeologyActivityStd.this, PaymentActivity.class);
                                intent.putExtra("videoUrl", videoUrl);
                                startActivity(intent);
                            }
                        }
                    });
        });
*/
        videoAdapter.setOnItemClickListener(video -> {
            String videoUrl = video.getVideoUrl();
            String videoName = video.getVideoName();
            String videoPrice = video.getPrice();
            String videoSubject = video.getSubject();
            String videoGradeYear = video.getGradeYear();

            db.collection("paymentCodes").whereArrayContains("videos", videoUrl).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                Intent intent = new Intent(BiologyActivitySTD.this, VideoPlayerActivity.class);
                                intent.putExtra("videoUrl", videoUrl);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(BiologyActivitySTD.this, PaymentMonthBio.class);
                                intent.putExtra("videoUrl", videoUrl);
                                intent.putExtra("videoName", videoName);
                                intent.putExtra("videoPrice", videoPrice);
                                intent.putExtra("videoSubject", videoSubject);
                                intent.putExtra("videoGradeYear", videoGradeYear);
                                startActivity(intent);
                            }
                        }
                    });
        });

        subscribeButton.setOnClickListener(v -> {
            List<Video> selectedVideos = videoAdapter.getSelectedVideos();

            if (selectedVideos.size() == MAX_SELECTION) {
                ArrayList<String> videoUrls = new ArrayList<>();
                ArrayList<String> videoNames = new ArrayList<>();
                ArrayList<String> videoPrices = new ArrayList<>();
                ArrayList<String> videoSubjects = new ArrayList<>();
                ArrayList<String> videoGradeYears = new ArrayList<>();
                int totalAmount = 0;
                for (Video video : selectedVideos) {
                    videoUrls.add(video.getVideoUrl());
                    videoNames.add(video.getVideoName());
                    videoPrices.add(video.getPrice());
                    videoSubjects.add(video.getSubject());
                    videoGradeYears.add(video.getGradeYear());
                    totalAmount += Integer.parseInt(video.getPrice()); // assuming video price is stored as String
                }
                Intent intent = new Intent(BiologyActivitySTD.this, PaymentMonthBio.class);
                intent.putStringArrayListExtra("videoUrls", videoUrls);
                intent.putStringArrayListExtra("videoNames", videoNames);
                intent.putStringArrayListExtra("videoPrices", videoPrices);
                intent.putStringArrayListExtra("videoSubjects", videoSubjects);
                intent.putStringArrayListExtra("videoGradeYears", videoGradeYears);
                intent.putExtra("subscriptionType", "monthly");
                intent.putExtra("totalAmount", totalAmount); // pass total amount
                startActivity(intent);
            } else {
                Toast.makeText(BiologyActivitySTD.this, "يجب اختيار 8 فيديوهات فقط", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
