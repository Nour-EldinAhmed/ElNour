package com.example.elnour;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elnour.Biology.FileModelsBiology;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FileAdapter adapter;
    private List<FileModelsBiology> files;
    private static final int REQUEST_WRITE_STORAGE = 113;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);


        recyclerView = findViewById(R.id.recycler_view_files);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        files = new ArrayList<>();
        adapter = new FileAdapter(this, files);
        recyclerView.setAdapter(adapter);

        loadFilesFromFirestore();
    }

    private void loadFilesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("files")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FileModelsBiology file = document.toObject(FileModelsBiology.class);
                            files.add(file);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle errors
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // الإذن منح
            } else {
                Toast.makeText(this, "Permission required to download files", Toast.LENGTH_LONG).show();
            }
        }
    }

}
