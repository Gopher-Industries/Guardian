package deakin.gopher.guardian.view.general;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.view.patient.dailyreport.DailyReportActivity;

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
}
