package com.example.tg_patient_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button careplan1 = findViewById(R.id.careplan1);
        Button careplan2 = findViewById(R.id.careplan2);
        Button careplan3 = findViewById(R.id.careplan3);
        ImageButton newPatientButton = findViewById(R.id.NewPatientButton);

        careplan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent careplan1intent = new Intent(HomeActivity.this, Careplan1.class);
                startActivity(careplan1intent);
            }
        });
        careplan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent careplan1intent = new Intent(HomeActivity.this, Careplan2.class);
                startActivity(careplan1intent);
            }
        });
        careplan3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent careplan1intent = new Intent(HomeActivity.this, Careplan3.class);
                startActivity(careplan1intent);
            }
        });
        newPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPatientIntent = new Intent(HomeActivity.this, NewPatientActivity.class);
                startActivity(newPatientIntent);
            }
        });
    }
}