package com.example.tg_patient_profile.view.caretaker.notifications.confirmincident;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.caretaker.CaretakerDashboardActivity;
import com.example.tg_patient_profile.view.caretaker.notifications.falsealarm.FalseAlertConfirmedActivity;
import com.example.tg_patient_profile.view.caretaker.notifications.falsealarm.FalseAlertConfirmedDialogActivity;

public class ConfirmIncidentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmincident);
    }

    public void onConfirmIncidentCancelClick(View view) {
        Intent medicalDiagnosticsActivityIntent = new Intent(ConfirmIncidentActivity.this, CaretakerDashboardActivity.class);
        startActivity(medicalDiagnosticsActivityIntent);
    }

}