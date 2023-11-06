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

    //        Intent intent = getIntent();
    //        String carePlan = intent.getStringExtra(Util.CARE_PLAN_TYPE);
    //        String nutritionHydration = intent.getStringExtra(Util.NUTRITION_HYDRATION_TYPE);
    //        String[] supportReqArray = intent.getStringArrayExtra(Util.SUPPORT_REQUIREMENTS);
    //
    //        if (supportReqArray.length != 0) {
    //            for (int i = 0; i < supportReqArray.length - 1; i++) {
    //                supportReq.append(supportReqArray[i] + "\n");
    //            }
    //            supportReq.append(supportReqArray[supportReqArray.length-1]);
    //        }
    //
    //        String dietTiming = intent.getStringExtra(Util.DIET_TIMING);
    //        String[] drinkLikesArray = intent.getStringArrayExtra(Util.DRINK_LIKES);
    //
    //        if (drinkLikesArray.length != 0) {
    //            for (int i = 0; i < drinkLikesArray.length - 1; i++) {
    //                drinkLikes.append(drinkLikesArray[i] + "\n");
    //            }
    //            drinkLikes.append(drinkLikesArray[drinkLikesArray.length-1]);
    //        }
    //
    //        String sleepPattern = intent.getStringExtra(Util.SLEEP_PATTERN);
    //        String[] painArray = intent.getStringArrayExtra(Util.PAIN);
    //        if (painArray.length != 0) {
    //            for (int i = 0; i < painArray.length - 1; i++) {
    //                pain.append(painArray[i] + "\n");
    //            }
    //            pain.append(painArray[painArray.length-1]);
    //        }
    //        String[] behaviorArray = intent.getStringArrayExtra(Util.BEHAVIOUR_MANAGEMENT);
    //
    //        if (behaviorArray.length != 0) {
    //            for (int i = 0; i < behaviorArray.length - 1; i++) {
    //                behaviour.append(behaviorArray[i] + "\n");
    //            }
    //            behaviour.append(behaviorArray[behaviorArray.length-1]);
    //        }
    //        float painScore = Float.parseFloat(intent.getStringExtra(Util.PAIN_SCORE));
    //
    //        carePlanSummary.setText(carePlan);
    //        nutritionHydrationSummary.setText(nutritionHydration);
    //        supportReqSummary.setText(supportReq);
    //        dietTimingSummary.setText(dietTiming);
    //        drinkLikesSummary.setText(drinkLikes);
    //        sleepPatternSummary.setText(sleepPattern);
    //        painSummary.setText(pain);
    //        painScoreSummary.setRating(painScore);
    //        behaviorSummary.setText(behaviour);
    prev_button.setOnClickListener(
            view -> startActivity(new Intent(CarePlanSummaryActivity.this, CarePlanActivity.class)));
  }
}
