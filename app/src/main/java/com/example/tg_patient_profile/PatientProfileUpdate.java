package com.example.tg_patient_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class PatientProfileUpdate extends AppCompatActivity {

    Button nextPatientProfileUpdateButton;
    Button previousPatientProfileUpdateButton;
    Button savePatientProfileUpdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile_update);

        nextPatientProfileUpdateButton = (Button) findViewById(R.id.nextPatientProfileUpdateButton);
        previousPatientProfileUpdateButton = (Button) findViewById(R.id.previousPatientProfileUpdateButton);
        savePatientProfileUpdateButton = (Button) findViewById(R.id.savePatientProfileUpdateButton);

        nextPatientProfileUpdateButton.setVisibility(View.VISIBLE);
        previousPatientProfileUpdateButton.setVisibility(View.INVISIBLE);
        savePatientProfileUpdateButton.setVisibility(View.INVISIBLE);
    }


    public void selectFragmentPatientProfileUpdateClick(View view) {

        Fragment fragment;

        switch (view.getId()) {
            case (R.id.previousPatientProfileUpdateButton):
                fragment = new PatientProfileUpdate1Fragment();
                nextPatientProfileUpdateButton.setVisibility(View.VISIBLE);
                previousPatientProfileUpdateButton.setVisibility(View.INVISIBLE);
                savePatientProfileUpdateButton.setVisibility(View.INVISIBLE);
                break;

            case R.id.nextPatientProfileUpdateButton:
                fragment = new PatientProfileUpdate2Fragment();
                nextPatientProfileUpdateButton.setVisibility(View.INVISIBLE);
                previousPatientProfileUpdateButton.setVisibility(View.VISIBLE);
                savePatientProfileUpdateButton.setVisibility(View.VISIBLE);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment, fragment).commit();
    }
}