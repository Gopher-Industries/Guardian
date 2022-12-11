package com.example.tg_patient_profile.view.caretaker.notifications.confirmincident;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.caretaker.CaretakerDashboardActivity;
import com.example.tg_patient_profile.view.patient.patientdata.healthandwelfare.HealthAndWelfareFragment1;

public class ConfirmIncidentActivity extends AppCompatActivity {

    Spinner hospitalSpinner;
    String hospitalSelection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_incident);

        hospitalSpinner = findViewById(R.id.hospitalSpinner);

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> hospitalAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.hospital_list_array, R.layout.spinner_layout);

        // Apply the adapter to the Spinner
        hospitalSpinner.setAdapter(hospitalAdapter);

        //Set the Spinner to the list of choices
        hospitalSpinner.setOnItemSelectedListener(new SpinnerActivity());

        hospitalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                hospitalSelection = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onConfirmIncidentCancelClick(View view) {
        Intent medicalDiagnosticsActivityIntent = new Intent(ConfirmIncidentActivity.this, CaretakerDashboardActivity.class);
        startActivity(medicalDiagnosticsActivityIntent);
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}