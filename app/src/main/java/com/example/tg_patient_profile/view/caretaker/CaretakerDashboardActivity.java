package com.example.tg_patient_profile.view.caretaker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.caretaker.notifications.FallAlertActivity;
import com.example.tg_patient_profile.view.general.PatientListActivity;
import com.example.tg_patient_profile.view.general.PatientProfileActivity;

public class CaretakerDashboardActivity extends AppCompatActivity {

  TextView selectAPatientTextView;

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_caretaker_dashboard);

    selectAPatientTextView = findViewById(R.id.selectAPatientTextView);
  }

  public void onHealthDataClick(final View view) {
    final Intent healthDataActivityIntent =
        new Intent(CaretakerDashboardActivity.this, PatientProfileActivity.class);
    startActivity(healthDataActivityIntent);
  }

  public void onNotificationsClick(final View view) {
    final Intent medicalDiagnosticsActivityIntent =
        new Intent(CaretakerDashboardActivity.this, FallAlertActivity.class);
    startActivity(medicalDiagnosticsActivityIntent);
  }

  public void onSelectAPatientClick(final View view) {
    final Intent patientProfileListIntent =
        new Intent(CaretakerDashboardActivity.this, PatientListActivity.class);
    startActivity(patientProfileListIntent);
  }
}
