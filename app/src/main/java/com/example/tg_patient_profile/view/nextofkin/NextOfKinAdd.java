package com.example.tg_patient_profile.view.nextofkin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.GP.GP_Add;
import com.example.tg_patient_profile.view.patient.PatientAdd;

public class NextOfKinAdd extends AppCompatActivity {

    Button prev_button, next_button;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_of_kin_add);
        prev_button = findViewById(R.id.previousNextOfKinPrevButton);
        next_button = findViewById(R.id.nextNextOfKinNextButton);
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NextOfKinAdd.this, PatientAdd.class));
            }
        });
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NextOfKinAdd.this, GP_Add.class));

            }
        });
    }
}