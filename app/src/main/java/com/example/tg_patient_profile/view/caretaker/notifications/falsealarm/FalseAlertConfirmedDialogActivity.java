package com.example.tg_patient_profile.view.caretaker.notifications.falsealarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tg_patient_profile.R;

public class FalseAlertConfirmedDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_false_alert_confirmed);
    }

    public void onFalseAlertConfirmedClick(View view) {
        Intent medicalDiagnosticsActivityIntent = new Intent(FalseAlertConfirmedDialogActivity.this, FalseAlertConfirmedActivity.class);
        startActivity(medicalDiagnosticsActivityIntent);
    }
}