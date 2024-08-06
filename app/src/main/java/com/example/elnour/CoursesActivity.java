package com.example.elnour;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.MediaController;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import android.widget.Toast;
/*
import android.widget.Toast;

import com.example.elnour.databinding.ActivityCoursesBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
*/
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import io.paperdb.Paper;

public class CoursesActivity extends AppCompatActivity  {
    private static final String TAG = "CoursesActivity";
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private ArrayList<FileModel> videoList;
    private ItemClickListener listener;
    DatabaseReference reference;
    String fileModel;
    FileModel fileModelx;
    FileModel fpay;
    MediaController controller;

    FloatingActionButton btn_float,bf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        reference= FirebaseDatabase.getInstance().getReference("videos");
        Paper.init(this);
        // controller=new MediaController(this);
        btn_float=findViewById(R.id.btn_float);
        recyclerView = findViewById(R.id.rec_video);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //  bf=findViewById(R.id.btn_float_del);
        videoList = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren())
                {
                    fileModelx=datasnapshot.getValue(FileModel.class);

                    // FileModel fileModel1=new FileModel();
                    //  fileModel1.setTitle(fileModel);
                    //   Toast.makeText(CoursesActivity.this, fileModelx.getPrice(), Toast.LENGTH_SHORT).show();

                    videoList.add(fileModelx);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseStorage.getInstance().getReference("videos").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(ListResult listResult) {
/*
                adapter=new VideoAdapter(CoursesActivity.this,videoList);
                adapter.setOnItemClickListener(new ItemClickListener() {
                    @Override
                    public void ItemClcik(FileModel fileModel) {
                        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(fileModel.getVideourl()));
                        intent.setDataAndType(Uri.parse(fileModel.getVideourl()),"video/*");
                        startActivity(intent);
                    }
                });
*/
                recyclerView.setAdapter(adapter);
                listResult.getItems().forEach(new Consumer<StorageReference>() {
                    @Override
                    public void accept(StorageReference storageReference) {
                        FileModel video = new FileModel();

                        //  video.setTitle(storageReference.getName());


                        //    Toast.makeText(CoursesActivity.this, video.getPrice(), Toast.LENGTH_SHORT).show();

                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String url = "https://" + task.getResult().getEncodedAuthority() + task.getResult().getEncodedPath() + "?alt=media&token=" + task.getResult().getQueryParameters("token").get(0);
                                video.setVideourl(url);
                                /*

                                video.setPrice(video.getPrice());


                               Toast.makeText(CoursesActivity.this, video.getTitle(), Toast.LENGTH_SHORT).show();*/
                                //   videoList.add(video);
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }


                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CoursesActivity.this, "Faild", Toast.LENGTH_SHORT).show();
            }

        });


        btn_float.setOnClickListener(view -> {

            Intent intent=new Intent(this, TestUpload_Browse.class);
            startActivity(intent);
        });

    }

    public void showDialog()
    {
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_payment);
        Button btn_pay=dialog.findViewById(R.id.payment);
        Button btn_cancel=dialog.findViewById(R.id.cancel);

        btn_pay.setOnClickListener(view -> {
            fpay=new FileModel();
          //  adapter.deleteVideo(fpay);
            adapter.notifyDataSetChanged();


        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


}


