package deakin.gopher.guardian.view.caretaker.notifications.falsealarm;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.view.caretaker.CaretakerDashboardActivity;
import deakin.gopher.guardian.view.general.BaseActivity;

public class FalseAlertConfirmedActivity extends BaseActivity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_confirm_false_alert);

    final Button submitFalseAlertButton = findViewById(R.id.submitFalseAlert);

    submitFalseAlertButton.setOnClickListener(v -> showDialog());
  }

  private void showDialog() {
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.layout_false_alert_confirmed);
    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    final Button okButtonFalseAlert = dialog.findViewById(R.id.okButtonFalseAlert);
    okButtonFalseAlert.setOnClickListener(
        v -> {
          final Intent intent =
              new Intent(getApplicationContext(), CaretakerDashboardActivity.class);
          startActivity(intent);
          finish();
        });
    dialog.show();
  }
}
