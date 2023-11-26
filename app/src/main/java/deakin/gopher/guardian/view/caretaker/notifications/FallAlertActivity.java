package deakin.gopher.guardian.view.caretaker.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.view.caretaker.notifications.confirmincident.ConfirmIncidentActivity;
import deakin.gopher.guardian.view.caretaker.notifications.falsealarm.FalseAlertConfirmedActivity;
import deakin.gopher.guardian.view.general.BaseActivity;

public class FallAlertActivity extends BaseActivity {

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
