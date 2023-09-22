package com.example.tg_patient_profile.view.general;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

//import com.example.tg_patient_profile.view.patient.PatientAdd;
import com.example.tg_patient_profile.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;


import com.example.tg_patient_profile.view.patient.dailyreport.DailyReportActivity;
public class Homepage4caretaker extends AppCompatActivity {

    Button patientListButton, settingsButton, signOutButton;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage4caretaker);

        patientListButton = findViewById(R.id.patientListButton);
        settingsButton = findViewById(R.id.settingsButton);
        signOutButton = findViewById(R.id.sighOutButton);


        //patient list button
        patientListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent medicalDiagnosticsActivityIntent = new Intent(Homepage4caretaker.this, PatientListActivity.class);
                startActivity(medicalDiagnosticsActivityIntent);
            }
        });

        //settings button
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent medicalDiagnosticsActivityIntent = new Intent(Homepage4caretaker.this, Setting.class);
                startActivity(medicalDiagnosticsActivityIntent);
            }
        });

        //sign out button
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent medicalDiagnosticsActivityIntent = new Intent(Homepage4caretaker.this, LoginActivity.class);
                startActivity(medicalDiagnosticsActivityIntent);
            }
        });
    }
}