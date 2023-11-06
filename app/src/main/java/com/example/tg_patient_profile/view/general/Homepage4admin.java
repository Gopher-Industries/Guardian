package com.example.tg_patient_profile.view.general;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.patient.dailyreport.DailyReportActivity;

public class Homepage4admin extends AppCompatActivity {

  Button newPatientButton, dailyReportButton, patientListButton, settingsButton, signOutButton;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_homepage4admin);

    newPatientButton = findViewById(R.id.newPaitentButton);
    dailyReportButton = findViewById(R.id.dailyReportButton4caretaker);
    patientListButton = findViewById(R.id.patientListButton);
    settingsButton = findViewById(R.id.settingsButton);
    signOutButton = findViewById(R.id.sighOutButton);

    // new Patient Button
    newPatientButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4admin.this, PatientProfileAddActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });

    // daily report button
    dailyReportButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4admin.this, DailyReportActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });
    // patient list button
    patientListButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4admin.this, PatientListActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });

    // settings button
    settingsButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4admin.this, Setting.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });

    // sign out button
    signOutButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4admin.this, LoginActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });
  }

  //    public void onNotificationsClick(View view) {
  //        Intent medicalDiagnosticsActivityIntent = new Intent(CaretakerDashboardActivity.this,
  // FallAlertActivity.class);
  //        startActivity(medicalDiagnosticsActivityIntent);
  //    }
  //
  //    //Listener for the button to add orders
  //    public void onSelectAPatientClick(View view) {
  //        Intent patientProfileListIntent = new Intent(CaretakerDashboardActivity.this,
  // PatientListActivity.class);
  //        startActivity(patientProfileListIntent);
  //    }
  //
  //    //Listener for the button to add orders
  //    public void onCaretakerProfileEditClick(View view) {
  //        Intent patientProfileListIntent = new Intent(CaretakerDashboardActivity.this,
  // CaretakerProfileActivity.class);
  //        startActivity(patientProfileListIntent);
  //    }
}
