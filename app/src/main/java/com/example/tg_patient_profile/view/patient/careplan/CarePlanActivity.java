package com.example.tg_patient_profile.view.patient.careplan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.util.Util;
import com.xeoh.android.checkboxgroup.CheckBoxGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class CarePlanActivity extends AppCompatActivity {

    RadioGroup carePlanTypeGroup, nutritionHydrationGroup, dietTimingsGroup, sleepPatternsGroup;
    HashMap<CheckBox, String> supportRequirementsCheckBox = new HashMap<>();
    HashMap<CheckBox, String> drinkLikesCheckBox = new HashMap<>();
    HashMap<CheckBox, String> painCheckBox = new HashMap<>();
    HashMap<CheckBox, String> behavioralManagementCheckBox = new HashMap<>();

    String carePlanType = "";
    String nutritionHyrdration= "";
    String dietTiming= "";
    String sleepPattern= "";
    String painScore = "";
    ArrayList<String> supportRequirements = new ArrayList<>();
    ArrayList<String> drinkLikes = new ArrayList<>();;
    ArrayList<String> behavioralManagement = new ArrayList<>();
    ArrayList<String>painList = new ArrayList<>();

    RatingBar painRatingBar;

    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_plan);

        submitButton = findViewById(R.id.carePlanSubmitButton);

        // care plan

        carePlanTypeGroup = (RadioGroup) findViewById(R.id.carePlanTypeRGroup);

        carePlanTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = findViewById(checkedId);
                carePlanType = button.getText().toString();
            }
        });

        // nutrition hydration

        nutritionHydrationGroup = (RadioGroup) findViewById(R.id.nutritionHydrationRGroup);

        nutritionHydrationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = findViewById(checkedId);
                nutritionHyrdration = button.getText().toString();
            }
        });

        // diet timing

        dietTimingsGroup = (RadioGroup) findViewById(R.id.dietTimingRGroup);

        dietTimingsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = findViewById(checkedId);
                dietTiming = button.getText().toString();
            }
        });

        sleepPatternsGroup = (RadioGroup) findViewById(R.id.sleepPatternsRGroup);

        sleepPatternsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = findViewById(checkedId);
                sleepPattern = button.getText().toString();
            }
        });

        // rating bar

        painRatingBar = findViewById(R.id.painRatingBar);
        painRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                painScore = Float.toString(rating);
            }
        });

        // suport requirements

        supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.selfCareCheckBox), "Self Care");
        supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.walkingCheckBox), "Walking");
        supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.activitiesCheckBox), "Activities");
        supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.bathroomCheckBox), "Bathroom");
        supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.shoppingCheckBox), "Shopping");

        CheckBoxGroup<String> checkBoxGroup = new CheckBoxGroup<>(supportRequirementsCheckBox,
                new CheckBoxGroup.CheckedChangeListener<String>() {
                    @Override
                    public void onCheckedChange(ArrayList<String> values) {
                        supportRequirements = values;
                        Log.i("masuk", "masuk");
                    }
                });

        // drink likes

        drinkLikesCheckBox.put((CheckBox) findViewById(R.id.coffeeCheckBox), "Coffee");
        drinkLikesCheckBox.put((CheckBox) findViewById(R.id.teaCheckBox), "Tea");
        drinkLikesCheckBox.put((CheckBox) findViewById(R.id.waterCheckBox), "Water");

        CheckBoxGroup<String> drinkLikesGroup = new CheckBoxGroup<>(drinkLikesCheckBox,
                new CheckBoxGroup.CheckedChangeListener<String>() {
                    @Override
                    public void onCheckedChange(ArrayList<String> values) {
                        drinkLikes = values;
                    }
                });

        // pain

        painCheckBox.put((CheckBox) findViewById(R.id.chronicCheckBox), "Chronic");
        painCheckBox.put((CheckBox) findViewById(R.id.massageRequiredCheckBox), "Massage Required");
        painCheckBox.put((CheckBox) findViewById(R.id.pillowSupportCheckBox), "Pillow Support");
        painCheckBox.put((CheckBox) findViewById(R.id.heatPackCheckBox), "Heat Pack");
        painCheckBox.put((CheckBox) findViewById(R.id.NAcheckBox), "NA");

        CheckBoxGroup<String> painGroup = new CheckBoxGroup<>(painCheckBox,
                new CheckBoxGroup.CheckedChangeListener<String>() {
                    @Override
                    public void onCheckedChange(ArrayList<String> values) {
                        painList = values;
                    }
                });

        // behavior


        behavioralManagementCheckBox.put((CheckBox) findViewById(R.id.dollTherapyCheckBox), "Doll Therapy");
        behavioralManagementCheckBox.put((CheckBox) findViewById(R.id.petTherapyCheckBox), "Pet Therapy");
        behavioralManagementCheckBox.put((CheckBox) findViewById(R.id.dementiaCheckBox), "Dementia Management");
        behavioralManagementCheckBox.put((CheckBox) findViewById(R.id.lightsOnBedroomCB), "Lights on Bedroom");
        behavioralManagementCheckBox.put((CheckBox) findViewById(R.id.lightsOnBathroomCB), "Lights on Bathroom");

        CheckBoxGroup<String> behavioralManagementGroup = new CheckBoxGroup<>(behavioralManagementCheckBox,
                new CheckBoxGroup.CheckedChangeListener<String>() {
                    @Override
                    public void onCheckedChange(ArrayList<String> values) {
                        behavioralManagement = values;
                    }
                });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CarePlanSummaryActivity.class);
                intent.putExtra(Util.CARE_PLAN_TYPE, carePlanType);
                intent.putExtra(Util.NUTRITION_HYDRATION_TYPE, nutritionHyrdration);
                intent.putExtra(Util.SUPPORT_REQUIREMENTS, supportRequirements.toArray(new String[0]));
                intent.putExtra(Util.DIET_TIMING, dietTiming);
                intent.putExtra(Util.DRINK_LIKES, drinkLikes.toArray(new String[0]));
                intent.putExtra(Util.SLEEP_PATTERN, sleepPattern);
                intent.putExtra(Util.PAIN, painList.toArray(new String[0]));
                intent.putExtra(Util.PAIN_SCORE, painScore);
                intent.putExtra(Util.BEHAVIOUR_MANAGEMENT, behavioralManagement.toArray(new String[0]));
                startActivity(intent);
            }
        });



    }
}