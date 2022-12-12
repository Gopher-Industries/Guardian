package com.example.tg_patient_profile.view.caretaker.notifications.falsealarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.caretaker.CaretakerDashboardActivity;

public class FalseAlertConfirmedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_false_alert);
    }

    public void onFalseAlertConfirmedCancelClick(View view) {
        Intent medicalDiagnosticsActivityIntent = new Intent(FalseAlertConfirmedActivity.this, CaretakerDashboardActivity.class);
        startActivity(medicalDiagnosticsActivityIntent);
    }

}