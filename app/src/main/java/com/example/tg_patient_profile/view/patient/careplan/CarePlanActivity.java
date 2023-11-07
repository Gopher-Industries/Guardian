package com.example.tg_patient_profile.view.patient.careplan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tg_patient_profile.R;
import java.util.HashMap;

public class CarePlanActivity extends AppCompatActivity {
  final HashMap<CheckBox, String> supportRequirementsCheckBox = new HashMap<>();
  final HashMap<CheckBox, String> drinkLikesCheckBox = new HashMap<>();
  final HashMap<CheckBox, String> painCheckBox = new HashMap<>();
  final HashMap<CheckBox, String> behavioralManagementCheckBox = new HashMap<>();

    Button submitButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_care_plan);

    submitButton = findViewById(R.id.carePlanSubmitButton);

    // care plan
    supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.selfCareCheckBox), "Self Care");
    supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.walkingCheckBox), "Walking");
    supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.activitiesCheckBox), "Activities");
    supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.bathroomCheckBox), "Bathroom");
    supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.shoppingCheckBox), "Shopping");

    drinkLikesCheckBox.put((CheckBox) findViewById(R.id.coffeeCheckBox), "Coffee");
    drinkLikesCheckBox.put((CheckBox) findViewById(R.id.teaCheckBox), "Tea");
    drinkLikesCheckBox.put((CheckBox) findViewById(R.id.waterCheckBox), "Water");

    painCheckBox.put((CheckBox) findViewById(R.id.chronicCheckBox), "Chronic");
    painCheckBox.put((CheckBox) findViewById(R.id.massageRequiredCheckBox), "Massage Required");
    painCheckBox.put((CheckBox) findViewById(R.id.pillowSupportCheckBox), "Pillow Support");
    painCheckBox.put((CheckBox) findViewById(R.id.heatPackCheckBox), "Heat Pack");
    painCheckBox.put((CheckBox) findViewById(R.id.NAcheckBox), "NA");

    // behavior

    behavioralManagementCheckBox.put(
        (CheckBox) findViewById(R.id.dollTherapyCheckBox), "Doll Therapy");
    behavioralManagementCheckBox.put(
        (CheckBox) findViewById(R.id.petTherapyCheckBox), "Pet Therapy");
    behavioralManagementCheckBox.put(
        (CheckBox) findViewById(R.id.dementiaCheckBox), "Dementia Management");
    behavioralManagementCheckBox.put(
        (CheckBox) findViewById(R.id.lightsOnBedroomCB), "Lights on Bedroom");
    behavioralManagementCheckBox.put(
        (CheckBox) findViewById(R.id.lightsOnBathroomCB), "Lights on Bathroom");

    submitButton.setOnClickListener(
        v -> {
          final AlertDialog.Builder builder =
              new AlertDialog.Builder(v.getContext()); // new AlertDialog.Builder(this);
          builder.setTitle("Saving Changes?");
          builder.setPositiveButton(
              "YES",
              (dialog, whichButton) -> {
                final Intent intent =
                    new Intent(getApplicationContext(), CarePlanSummaryActivity.class);
                startActivity(intent);
              });
          builder.setNegativeButton("No", null);

          final AlertDialog dialog = builder.create();
          dialog.setOnShowListener(
              arg0 -> {
                dialog
                    .getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(getResources().getColor(R.color.colorGreen));
                dialog
                    .getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(getResources().getColor(R.color.colorRed));
              });
          dialog.show();
        });
  }
}
