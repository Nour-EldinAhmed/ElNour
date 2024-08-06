package com.example.elnour.Geology;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class GeologyVideosActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_geology_videos);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.rec_videogeo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(videoList, this);
        recyclerView.setAdapter(videoAdapter);
        findViewById(R.id.btn_floatgeo).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), GeoUpload.class);
            startActivity(intent);
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("videos/Geology");
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
                Toast.makeText(GeologyVideosActivity.this, "Failed to load videos", Toast.LENGTH_SHORT).show();
            }
        });

        videoAdapter.setOnItemClickListener(video -> {
            String videoUrl = video.getVideoUrl();

            Intent intent = new Intent(GeologyVideosActivity.this, VideoPlayerActivity.class);
            intent.putExtra("videoUrl", videoUrl);
            startActivity(intent);
        });


    }
}
