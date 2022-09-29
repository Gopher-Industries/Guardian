package com.example.tg_patient_profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Careplan3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_careplan3);

        Button careplan3NextButton = findViewById(R.id.careplanNextButton);
        Button careplan3BackButton = findViewById(R.id.careplanPreviousButton);

        careplan3NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Careplan3.this, "Next screen not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        careplan3BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }
}