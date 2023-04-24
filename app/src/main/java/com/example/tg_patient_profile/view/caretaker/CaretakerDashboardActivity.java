package com.example.tg_patient_profile.view.caretaker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.general.AddNewPatientActivity;
import com.example.tg_patient_profile.view.general.LoginActivity;
import com.example.tg_patient_profile.view.general.PatientListActivity;
import com.example.tg_patient_profile.view.patient.dailyreport.DailyReportActivity;


public class CaretakerDashboardActivity extends AppCompatActivity {

    Button newPatientButton, dailyReportButton, patientListButton, settingsButton, signOutButton;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caretaker_dashboard);
        newPatientButton = findViewById(R.id.newPaitentButton);
        dailyReportButton = findViewById(R.id.dailyReportButton4caretaker);
        patientListButton = findViewById(R.id.patientListButton);
        settingsButton = findViewById(R.id.settingsButton);
        signOutButton = findViewById(R.id.sighOutButton);

        //new Patient Button
        newPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent medicalDiagnosticsActivityIntent = new Intent(CaretakerDashboardActivity.this, AddNewPatientActivity.class);
                startActivity(medicalDiagnosticsActivityIntent);
            }
        });

        //daily report button
        dailyReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent medicalDiagnosticsActivityIntent = new Intent(CaretakerDashboardActivity.this, DailyReportActivity.class);
                startActivity(medicalDiagnosticsActivityIntent);
            }
        });
        //patient list button
        patientListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent medicalDiagnosticsActivityIntent = new Intent(CaretakerDashboardActivity.this, PatientListActivity.class);
                startActivity(medicalDiagnosticsActivityIntent);
            }
        });

        //settings button
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent medicalDiagnosticsActivityIntent = new Intent(CaretakerDashboardActivity.this, SettingActivity.class);
//                startActivity(medicalDiagnosticsActivityIntent);
            }
        });

        //sign out button
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent medicalDiagnosticsActivityIntent = new Intent(CaretakerDashboardActivity.this, LoginActivity.class);
                startActivity(medicalDiagnosticsActivityIntent);
            }
        });


    }


//    public void onNotificationsClick(View view) {
//        Intent medicalDiagnosticsActivityIntent = new Intent(CaretakerDashboardActivity.this, FallAlertActivity.class);
//        startActivity(medicalDiagnosticsActivityIntent);
//    }
//
//    //Listener for the button to add orders
//    public void onSelectAPatientClick(View view) {
//        Intent patientProfileListIntent = new Intent(CaretakerDashboardActivity.this, PatientListActivity.class);
//        startActivity(patientProfileListIntent);
//    }
//
//    //Listener for the button to add orders
//    public void onCaretakerProfileEditClick(View view) {
//        Intent patientProfileListIntent = new Intent(CaretakerDashboardActivity.this, CaretakerProfileActivity.class);
//        startActivity(patientProfileListIntent);
//    }
}