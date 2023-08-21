package com.example.tg_patient_profile.view.patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.tg_patient_profile.view.general.UploadPhoto;
import com.example.tg_patient_profile.view.nextofkin.NextOfKinAdd;
import com.example.tg_patient_profile.R;

public class PatientAdd extends AppCompatActivity {

    Button next_button,AddImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_add);
        next_button = findViewById(R.id.add_patient_nextButton);
        AddImg=findViewById(R.id.addButton);



        AddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PatientAdd.this,UploadPhoto.class);
                startActivity(intent);

                String imageName = getIntent().getStringExtra("imgname");
                Uri imageUri = Uri.parse(imageName);

            }
        });



        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientAdd.this, NextOfKinAdd.class));
            }
        });
    }
}