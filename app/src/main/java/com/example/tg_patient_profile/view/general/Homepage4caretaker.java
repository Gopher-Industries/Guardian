package com.example.tg_patient_profile.view.general;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tg_patient_profile.R;

public class Homepage4caretaker extends AppCompatActivity {

    Button patientListButton, settingsButton, signOutButton;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage4caretaker);

        patientListButton = findViewById(R.id.patientListButton);
        settingsButton = findViewById(R.id.settingsButton3);
        signOutButton = findViewById(R.id.sighOutButton);


        //patient list button
        patientListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Intent medicalDiagnosticsActivityIntent = new Intent(Homepage4caretaker.this, PatientListActivity.class);
                startActivity(medicalDiagnosticsActivityIntent);
            }
        });

        //settings button
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Intent medicalDiagnosticsActivityIntent = new Intent(Homepage4caretaker.this, Setting.class);
                startActivity(medicalDiagnosticsActivityIntent);
            }
        });

        //sign out button
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Intent medicalDiagnosticsActivityIntent = new Intent(Homepage4caretaker.this, LoginActivity.class);
                startActivity(medicalDiagnosticsActivityIntent);
            }
        });
    }
}