package com.example.tg_patient_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Careplan2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_careplan2);

        Button careplan2PreviousButton = findViewById(R.id.careplanPreviousButton);
        Button careplan2NextButton = findViewById(R.id.careplanNextButton);

        careplan2PreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        careplan2NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent careplan3Intent = new Intent(Careplan2.this, Careplan3.class);
                startActivity(careplan3Intent);
            }
        });
    }
}