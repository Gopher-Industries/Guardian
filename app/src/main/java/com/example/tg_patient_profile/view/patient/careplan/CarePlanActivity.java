package com.example.tg_patient_profile.view.patient.careplan;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tg_patient_profile.R;
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
    String nutritionHyrdration = "";
    String dietTiming = "";
    String sleepPattern = "";
    String painScore = "";
    ArrayList<String> supportRequirements = new ArrayList<>();
    ArrayList<String> drinkLikes = new ArrayList<>();
    ArrayList<String> behavioralManagement = new ArrayList<>();
    ArrayList<String> painList = new ArrayList<>();

    RadioGroup painRatingGroup;

    Button submitButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_plan);

        submitButton = findViewById(R.id.carePlanSubmitButton);

        // care plan

        carePlanTypeGroup = findViewById(R.id.carePlanTypeRGroup);

        carePlanTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                final RadioButton button = findViewById(checkedId);
                carePlanType = button.getText().toString();
            }
        });

        // nutrition hydration

        nutritionHydrationGroup = findViewById(R.id.nutritionHydrationRGroup);

        nutritionHydrationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                final RadioButton button = findViewById(checkedId);
                nutritionHyrdration = button.getText().toString();
            }
        });

        // diet timing

        dietTimingsGroup = findViewById(R.id.dietTimingRGroup);

        dietTimingsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                final RadioButton button = findViewById(checkedId);
                dietTiming = button.getText().toString();
            }
        });

        sleepPatternsGroup = findViewById(R.id.sleepPatternsRGroup);

        sleepPatternsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                final RadioButton button = findViewById(checkedId);
                sleepPattern = button.getText().toString();
            }
        });

        // rating bar

        painRatingGroup = findViewById(R.id.ratingGroup);

        painRatingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                final RadioButton button = findViewById(checkedId);
                painScore = button.getText().toString();
            }
        });

        // suport requirements

        supportRequirementsCheckBox.put(findViewById(R.id.selfCareCheckBox), "Self Care");
        supportRequirementsCheckBox.put(findViewById(R.id.walkingCheckBox), "Walking");
        supportRequirementsCheckBox.put(findViewById(R.id.activitiesCheckBox), "Activities");
        supportRequirementsCheckBox.put(findViewById(R.id.bathroomCheckBox), "Bathroom");
        supportRequirementsCheckBox.put(findViewById(R.id.shoppingCheckBox), "Shopping");

        final CheckBoxGroup<String> checkBoxGroup = new CheckBoxGroup<>(supportRequirementsCheckBox,
                new CheckBoxGroup.CheckedChangeListener<String>() {
                    @Override
                    public void onCheckedChange(final ArrayList<String> values) {
                        supportRequirements = values;
                        Log.i("masuk", "masuk");
                    }
                });

        // drink likes

        drinkLikesCheckBox.put(findViewById(R.id.coffeeCheckBox), "Coffee");
        drinkLikesCheckBox.put(findViewById(R.id.teaCheckBox), "Tea");
        drinkLikesCheckBox.put(findViewById(R.id.waterCheckBox), "Water");

        final CheckBoxGroup<String> drinkLikesGroup = new CheckBoxGroup<>(drinkLikesCheckBox,
                new CheckBoxGroup.CheckedChangeListener<String>() {
                    @Override
                    public void onCheckedChange(final ArrayList<String> values) {
                        drinkLikes = values;
                    }
                });

        // pain

        painCheckBox.put(findViewById(R.id.chronicCheckBox), "Chronic");
        painCheckBox.put(findViewById(R.id.massageRequiredCheckBox), "Massage Required");
        painCheckBox.put(findViewById(R.id.pillowSupportCheckBox), "Pillow Support");
        painCheckBox.put(findViewById(R.id.heatPackCheckBox), "Heat Pack");
        painCheckBox.put(findViewById(R.id.NAcheckBox), "NA");

        final CheckBoxGroup<String> painGroup = new CheckBoxGroup<>(painCheckBox,
                new CheckBoxGroup.CheckedChangeListener<String>() {
                    @Override
                    public void onCheckedChange(final ArrayList<String> values) {
                        painList = values;
                    }
                });

        // behavior


        behavioralManagementCheckBox.put(findViewById(R.id.dollTherapyCheckBox), "Doll Therapy");
        behavioralManagementCheckBox.put(findViewById(R.id.petTherapyCheckBox), "Pet Therapy");
        behavioralManagementCheckBox.put(findViewById(R.id.dementiaCheckBox), "Dementia Management");
        behavioralManagementCheckBox.put(findViewById(R.id.lightsOnBedroomCB), "Lights on Bedroom");
        behavioralManagementCheckBox.put(findViewById(R.id.lightsOnBathroomCB), "Lights on Bathroom");

        final CheckBoxGroup<String> behavioralManagementGroup = new CheckBoxGroup<>(behavioralManagementCheckBox,
                new CheckBoxGroup.CheckedChangeListener<String>() {
                    @Override
                    public void onCheckedChange(final ArrayList<String> values) {
                        behavioralManagement = values;
                    }
                });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());//new AlertDialog.Builder(this);
                builder.setTitle("Saving Changes?");
                // builder.setMessage("Do you really want to whatever?");
                //builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        final Intent intent = new Intent(getApplicationContext(), CarePlanSummaryActivity.class);
//                intent.putExtra(Util.CARE_PLAN_TYPE, carePlanType);
//                intent.putExtra(Util.NUTRITION_HYDRATION_TYPE, nutritionHyrdration);
//                intent.putExtra(Util.SUPPORT_REQUIREMENTS, supportRequirements.toArray(new String[0]));
//                intent.putExtra(Util.DIET_TIMING, dietTiming);
//                intent.putExtra(Util.DRINK_LIKES, drinkLikes.toArray(new String[0]));
//                intent.putExtra(Util.SLEEP_PATTERN, sleepPattern);
//                intent.putExtra(Util.PAIN, painList.toArray(new String[0]));
//                intent.putExtra(Util.PAIN_SCORE, painScore);
//                intent.putExtra(Util.BEHAVIOUR_MANAGEMENT, behavioralManagement.toArray(new String[0]));
                        startActivity(intent);
                        //Toast.makeText(MainActivity.this, "Yaay", Toast.LENGTH_SHORT).show();
                    }
                });
//        builder.setNegativeButton(android.R.string.no, null);
//        AlertDialog show = builder.show();
                builder.setNegativeButton("No", null);

                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorGreen));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorRed));
                        //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
                    }
                });
                dialog.show();


            }
        });


    }
}