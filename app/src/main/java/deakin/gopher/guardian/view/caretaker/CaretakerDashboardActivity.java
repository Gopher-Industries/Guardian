package deakin.gopher.guardian.view.caretaker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.view.caretaker.notifications.FallAlertActivity;
import deakin.gopher.guardian.view.general.BaseActivity;
import deakin.gopher.guardian.view.general.PatientListActivity;
import deakin.gopher.guardian.view.general.PatientProfileActivity;

public class CaretakerDashboardActivity extends BaseActivity {
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_caretaker_dashboard);
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
