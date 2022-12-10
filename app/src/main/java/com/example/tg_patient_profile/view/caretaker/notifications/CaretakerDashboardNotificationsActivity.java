package com.example.tg_patient_profile.view.caretaker.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.caretaker.notifications.confirmincident.ConfirmIncidentActivity;
import com.example.tg_patient_profile.view.caretaker.notifications.falsealarm.FalseAlertConfirmedDialogActivity;

public class CaretakerDashboardNotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);
    }

    public void onNotificationsFalseAlarmClick(View view) {
        Intent medicalDiagnosticsActivityIntent = new Intent(CaretakerDashboardNotificationsActivity.this, FalseAlertConfirmedDialogActivity.class);
        startActivity(medicalDiagnosticsActivityIntent);
    }

    public void onNotificationsConfirmIncidentClick(View view) {
        Intent medicalDiagnosticsActivityIntent = new Intent(CaretakerDashboardNotificationsActivity.this, ConfirmIncidentActivity.class);
        startActivity(medicalDiagnosticsActivityIntent);
    }

}