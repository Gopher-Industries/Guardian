package com.example.tg_patient_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        Button getStartedButton = findViewById(R.id.getStartedButton);
        Button patientProfileButton = findViewById(R.id.patientProfileButton);
        Button GP_ProfileButton = findViewById(R.id.GP_ProfileButton);

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

        GP_ProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent patientProfileIntent = new Intent(MainActivity.this, GP_List.class);
                startActivity(patientProfileIntent);
            }
        });
    }
}