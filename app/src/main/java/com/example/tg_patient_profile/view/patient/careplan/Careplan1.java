package com.example.tg_patient_profile.view.patient.careplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tg_patient_profile.R;

public class Careplan1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_careplan1);

        Button careplan1NextButton = findViewById(R.id.careplanNextButton);

        careplan1NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent careplan2Intent = new Intent(Careplan1.this, Careplan2.class);
                startActivity(careplan2Intent);
            }
        });
    }
}