package com.example.tg_patient_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    ImageButton newPatientButton, dailyReportButton, editPatientDataButton,
            patientListButton, viewActivityDataButton, associateRadarButton,
    newUserButton, settingsButton, signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        newPatientButton = (ImageButton) findViewById(R.id.NewPatientButton);
        dailyReportButton = (ImageButton) findViewById(R.id.DailyReportButton);
        editPatientDataButton = (ImageButton) findViewById(R.id.EditPatientDataButton);
        patientListButton = (ImageButton) findViewById(R.id.PatientListButton);
        viewActivityDataButton = (ImageButton) findViewById(R.id.ViewActivityDataButton);
        associateRadarButton = (ImageButton) findViewById(R.id.AssociateRadarButton);
        newUserButton = (ImageButton) findViewById(R.id.NewUserButton);
        settingsButton = (ImageButton) findViewById(R.id.SettingsButton);
        signOutButton = (ImageButton) findViewById(R.id.SignOutButton);

        newPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPatientIntent = new Intent(HomeActivity.this, NewPatientActivity.class);
                startActivity(newPatientIntent);
            }
        });

        dailyReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dailyReportIntent = new Intent(HomeActivity.this, DailyReportActivity.class);
                startActivity(dailyReportIntent);
            }
        });

        editPatientDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        patientListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent patientProfileList = new Intent(HomeActivity.this, PatientProfileList.class);
                startActivity(patientProfileList);
            }
        });

        viewActivityDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        associateRadarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}