package com.example.elnour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.elnour.Student.STDSignIn;
import com.example.elnour.Student.STDSignUp;
import com.example.elnour.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding=ActivityMainBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());
       findViewById(R.id.layteacher).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent=new Intent(getApplicationContext(), SignIn.class);
               startActivity(intent);
           }
       });
        findViewById(R.id.laystudent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), STDSignIn.class);
                startActivity(intent);
            }
        });
    }
}