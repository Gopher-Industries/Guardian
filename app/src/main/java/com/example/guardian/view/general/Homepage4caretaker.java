package com.example.guardian.view.general;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guardian.R;

public class Homepage4caretaker extends AppCompatActivity {

  Button patientListButton, settingsButton, signOutButton;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_homepage4caretaker);

    patientListButton = findViewById(R.id.patientListButton);
    settingsButton = findViewById(R.id.settingsButton3);
    signOutButton = findViewById(R.id.sighOutButton);

    // patient list button
    patientListButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4caretaker.this, PatientListActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });

    // settings button
    settingsButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4caretaker.this, Setting.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });

    // sign out button
    signOutButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4caretaker.this, LoginActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });
  }
}
