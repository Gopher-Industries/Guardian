package com.example.tg_patient_profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        Button getStartedButton = findViewById(R.id.getStartedButton);
        Button patientProfileButton = findViewById(R.id.patientProfileButton);

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getStartedIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(getStartedIntent);
            }
        });

        patientProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent patientProfileIntent = new Intent(MainActivity.this, PatientProfileList.class);
                startActivity(patientProfileIntent);
            }
        });
    }
}