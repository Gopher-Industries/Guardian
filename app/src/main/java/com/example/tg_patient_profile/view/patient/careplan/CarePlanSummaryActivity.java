package com.example.tg_patient_profile.view.patient.careplan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tg_patient_profile.R;

public class CarePlanSummaryActivity extends AppCompatActivity {

  Button prev_button;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_care_plan_summary);

    carePlanSummary = findViewById(R.id.carePlanSummary);
    nutritionHydrationSummary = findViewById(R.id.nutritionHydrationSummary);
    supportReqSummary = findViewById(R.id.supportReqSummary);
    dietTimingSummary = findViewById(R.id.dietTimingSummary);
    drinkLikesSummary = findViewById(R.id.drinkLikesSummary);
    sleepPatternSummary = findViewById(R.id.sleepPatternSummary);
    painSummary = findViewById(R.id.painSummary);
    behaviorSummary = findViewById(R.id.behaviouralManagement);
    painScoreSummary = findViewById(R.id.painRatingBarSummary);
    prev_button = findViewById(R.id.carePlanPrevButton);

    prev_button.setOnClickListener(
        view -> startActivity(new Intent(CarePlanSummaryActivity.this, CarePlanActivity.class)));
  }
}
