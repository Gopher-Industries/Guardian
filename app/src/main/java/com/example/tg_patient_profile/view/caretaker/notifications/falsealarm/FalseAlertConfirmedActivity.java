package com.example.tg_patient_profile.view.caretaker.notifications.falsealarm;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.caretaker.CaretakerDashboardActivity;

public class FalseAlertConfirmedActivity extends AppCompatActivity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_confirm_false_alert);

    final Button submitFalseAlertButton = findViewById(R.id.submitFalseAlert);

    submitFalseAlertButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View v) {
            showDialog();
          }
        });
  }

  private void showDialog() {
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.layout_false_alert_confirmed);
    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    final Button okButtonFalseAlert = dialog.findViewById(R.id.okButtonFalseAlert);
    okButtonFalseAlert.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View v) {
            final Intent intent =
                new Intent(getApplicationContext(), CaretakerDashboardActivity.class);
            startActivity(intent);
            finish();
          }
        });
    dialog.show();
  }
}
