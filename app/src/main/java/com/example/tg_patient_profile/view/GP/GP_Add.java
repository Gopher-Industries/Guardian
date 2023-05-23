package com.example.tg_patient_profile.view.GP;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tg_patient_profile.view.nextofkin.NextOfKinAdd;
import com.example.tg_patient_profile.R;

public class GP_Add extends AppCompatActivity {

    Button prev_button, save_button;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gp_add);
        prev_button = findViewById(R.id.gp_add_prev_button);
        save_button = findViewById(R.id.gp_add_save_button);
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GP_Add.this, NextOfKinAdd.class));
            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}