package com.example.tg_patient_profile.view.caretaker.notifications.falsealarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.patient.PatientDashboardActivity;

public class FalseAlertConfirmedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_false_alert);

        Button submitFalseAlertButton = findViewById(R.id.submitFalseAlert);

        submitFalseAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_false_alert_confirmed);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button okButtonFalseAlert = (Button) dialog.findViewById(R.id.okButtonFalseAlert);
        okButtonFalseAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PatientDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }
}