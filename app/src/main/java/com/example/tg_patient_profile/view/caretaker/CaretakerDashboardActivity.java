package com.example.tg_patient_profile.view.caretaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.caretaker.notifications.FallAlertActivity;
import com.example.tg_patient_profile.view.general.PatientListActivity;

public class CaretakerDashboardActivity extends AppCompatActivity {

    TextView selectAPatientTextView;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caretaker_dashboard);

        selectAPatientTextView = (TextView) findViewById(R.id.selectAPatientTextView);
    }


    public void onNotificationsClick(View view) {
        Intent medicalDiagnosticsActivityIntent = new Intent(CaretakerDashboardActivity.this, FallAlertActivity.class);
        startActivity(medicalDiagnosticsActivityIntent);
    }

    //Listener for the button to add orders
    public void onSelectAPatientClick(View view) {
        Intent patientProfileListIntent = new Intent(CaretakerDashboardActivity.this, PatientListActivity.class);
        startActivity(patientProfileListIntent);
    }

    //Listener for the button to add orders
    public void onCaretakerProfileEditClick(View view) {
        Intent patientProfileListIntent = new Intent(CaretakerDashboardActivity.this, CaretakerProfileActivity.class);
        startActivity(patientProfileListIntent);
    }
}