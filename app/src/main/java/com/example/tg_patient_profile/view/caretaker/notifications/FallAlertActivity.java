package com.example.tg_patient_profile.view.caretaker.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.caretaker.notifications.confirmincident.ConfirmIncidentActivity;
import com.example.tg_patient_profile.view.caretaker.notifications.falsealarm.FalseAlertConfirmedActivity;

public class FallAlertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_alert);

        ImageButton confirmIncidentButton = findViewById(R.id.confirmIncidentButton);
        ImageButton falseAlarmButton = findViewById(R.id.falseAlarmButton);

        confirmIncidentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent medicalDiagnosticsActivityIntent = new Intent(FallAlertActivity.this, ConfirmIncidentActivity.class);
                startActivity(medicalDiagnosticsActivityIntent);
            }
        });

        falseAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent medicalDiagnosticsActivityIntent = new Intent(FallAlertActivity.this, FalseAlertConfirmedActivity.class);
                startActivity(medicalDiagnosticsActivityIntent);
            }
        });
    }
}