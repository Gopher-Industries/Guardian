package com.gopher.guardian.view.caretaker.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guardian.R;
import com.gopher.guardian.view.caretaker.notifications.confirmincident.ConfirmIncidentActivity;
import com.gopher.guardian.view.caretaker.notifications.falsealarm.FalseAlertConfirmedActivity;

public class FallAlertActivity extends AppCompatActivity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fall_alert);

    final ImageButton confirmIncidentButton = findViewById(R.id.confirmIncidentButton);
    final ImageButton falseAlarmButton = findViewById(R.id.falseAlarmButton);

    confirmIncidentButton.setOnClickListener(
        v -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(FallAlertActivity.this, ConfirmIncidentActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });

    falseAlarmButton.setOnClickListener(
        v -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(FallAlertActivity.this, FalseAlertConfirmedActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });
  }
}
