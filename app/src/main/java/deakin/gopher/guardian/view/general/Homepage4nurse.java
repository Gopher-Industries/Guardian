package deakin.gopher.guardian.view.general;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import deakin.gopher.guardian.R;

public class Homepage4nurse extends AppCompatActivity {

  Button tasksButton, settingsButton, signOutButton;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_homepage4nurse);

    tasksButton = findViewById(R.id.tasksButton_nurse);
    settingsButton = findViewById(R.id.settingsButton_nurse);
    signOutButton = findViewById(R.id.sighOutButton_nurse);

    // patient list button
      tasksButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4nurse.this, PatientListActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });

    // settings button
    settingsButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4nurse.this, Setting.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });

    // sign out button
    signOutButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4nurse.this, LoginActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });
  }
}
