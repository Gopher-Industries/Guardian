package com.gopher.guardian.view.patient.careplan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guardian.R;

public class CarePlanSummaryActivity extends AppCompatActivity {
  Button prevButton;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_care_plan_summary);
    prevButton = findViewById(R.id.carePlanPrevButton);

    prevButton.setOnClickListener(
        view -> startActivity(new Intent(CarePlanSummaryActivity.this, CarePlanActivity.class)));
  }
}
