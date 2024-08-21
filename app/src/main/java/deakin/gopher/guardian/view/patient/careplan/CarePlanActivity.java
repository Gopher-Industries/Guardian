package deakin.gopher.guardian.view.patient.careplan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.CarePlan;
import java.util.HashMap;

public class CarePlanActivity extends AppCompatActivity {
  final HashMap<CheckBox, String> supportRequirementsCheckBox = new HashMap<>();
  final HashMap<CheckBox, String> drinkLikesCheckBox = new HashMap<>();
  final HashMap<CheckBox, String> painCheckBox = new HashMap<>();
  final HashMap<CheckBox, String> behavioralManagementCheckBox = new HashMap<>();

  RadioGroup carePlanTypeRadioGroup;
  HashMap<RadioButton, String> carePlanTypesRadioButtons = new HashMap<>();

  RadioGroup nutritionRadioGroup;
  HashMap<RadioButton, String> nutritionRadioButtons = new HashMap<>();

  RadioGroup dietTimeRadioGroup;
  HashMap<RadioButton, String> dietTimeRadioButtons = new HashMap<>();

  RadioGroup sleepPatternRadioGroup;
  HashMap<RadioButton, String> sleepPatternRadioButtons = new HashMap<>();

  RadioGroup painScoreRadioGroup;
  HashMap<RadioButton, String> painScoreRadioButtons = new HashMap<>();

  Button submitButton;
  ImageView carePlanMenuButton;
  DatabaseReference carePlanRef;
  String patientId;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    patientId = getIntent().getStringExtra("patientId");
    setContentView(R.layout.activity_care_plan);

    submitButton = findViewById(R.id.carePlanSubmitButton);

    final NavigationView navigationView = findViewById(R.id.nav_view);
    carePlanMenuButton = findViewById(R.id.menuButton11);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    carePlanMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });

    // careplan type
    carePlanTypeRadioGroup = findViewById(R.id.carePlanTypeRGroup);
    carePlanTypesRadioButtons.put((RadioButton) findViewById(R.id.radioButton2), "Home");
    carePlanTypesRadioButtons.put((RadioButton) findViewById(R.id.radioButton), "Hospital");

    nutritionRadioGroup = findViewById(R.id.nutritionHydrationRGroup);
    nutritionRadioButtons.put((RadioButton) findViewById(R.id.radioButton6), "Yes");
    nutritionRadioButtons.put((RadioButton) findViewById(R.id.radioButton7), "No");
    nutritionRadioButtons.put((RadioButton) findViewById(R.id.radioButton8), "NA");

    dietTimeRadioGroup = findViewById((R.id.dietTimingRGroup));
    dietTimeRadioButtons.put((RadioButton) findViewById(R.id.radioButton9), "8AM-BR");
    dietTimeRadioButtons.put((RadioButton) findViewById(R.id.radioButton5), "12PM-LN");
    dietTimeRadioButtons.put((RadioButton) findViewById(R.id.radioButton10), "6PM-DN");

    sleepPatternRadioGroup = findViewById(R.id.sleepPatternsRGroup);
    sleepPatternRadioButtons.put((RadioButton) findViewById(R.id.radioButton61), "10PM-6AM");
    sleepPatternRadioButtons.put((RadioButton) findViewById(R.id.radioButton71), "8PM-4AM");
    sleepPatternRadioButtons.put((RadioButton) findViewById(R.id.radioButton81), "11PM-7AM");

    painScoreRadioGroup = findViewById(R.id.ratingGroup);
    painScoreRadioButtons.put((RadioButton) findViewById((R.id.radioButton21)), "1");
    painScoreRadioButtons.put((RadioButton) findViewById((R.id.radioButton22)), "2");
    painScoreRadioButtons.put((RadioButton) findViewById((R.id.radioButton23)), "3");
    painScoreRadioButtons.put((RadioButton) findViewById((R.id.radioButton24)), "4");
    painScoreRadioButtons.put((RadioButton) findViewById((R.id.radioButton25)), "5");
    painScoreRadioButtons.put((RadioButton) findViewById((R.id.radioButton26)), "6");
    painScoreRadioButtons.put((RadioButton) findViewById((R.id.radioButton27)), "7");
    painScoreRadioButtons.put((RadioButton) findViewById((R.id.radioButton28)), "8");
    painScoreRadioButtons.put((RadioButton) findViewById((R.id.radioButton29)), "9");
    painScoreRadioButtons.put((RadioButton) findViewById((R.id.radioButton30)), "10");

    // support requirements
    supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.selfCareCheckBox), "Self Care");
    supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.walkingCheckBox), "Walking");
    supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.activitiesCheckBox), "Activities");
    supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.bathroomCheckBox), "Bathroom");
    supportRequirementsCheckBox.put((CheckBox) findViewById(R.id.shoppingCheckBox), "Shopping");

    // drinks
    drinkLikesCheckBox.put((CheckBox) findViewById(R.id.coffeeCheckBox), "Coffee");
    drinkLikesCheckBox.put((CheckBox) findViewById(R.id.teaCheckBox), "Tea");
    drinkLikesCheckBox.put((CheckBox) findViewById(R.id.waterCheckBox), "Water");

    // pain
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

    // Firebase initialization
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    carePlanRef = database.getReference("careplan");
    // Load care plan summary data from Firebase
    if (!"".equals(patientId) && null != patientId) {
      loadCarePlanDataForPatient(patientId);
    }

    submitButton.setOnClickListener(
        v -> {
          final AlertDialog.Builder builder =
              new AlertDialog.Builder(v.getContext()); // new AlertDialog.Builder(this);
          builder.setTitle("Saving Changes?");
          builder.setPositiveButton(
              "YES",
              (dialog, whichButton) -> {
                CarePlan updatedCarePlan = createUpdatedCarePlan();
                // Save the updated CarePlan to Firebase
                saveCarePlanToFirebase(updatedCarePlan);

                //                final Intent intent =
                //                    new Intent(getApplicationContext(),
                // CarePlanSummaryActivity.class);
                //                startActivity(intent);
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

  private void loadCarePlanDataForPatient(final String patientId) {
    DatabaseReference patientRef = carePlanRef.child(patientId);
    patientRef.addListenerForSingleValueEvent(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
              CarePlan carePlan = dataSnapshot.getValue(CarePlan.class);
              if (carePlan != null) {

                String carePlanType = carePlan.carePlanType;
                selectRadioButton(carePlanTypeRadioGroup, carePlanTypesRadioButtons, carePlanType);

                String nutriHydration = carePlan.nutritionHydration;
                selectRadioButton(nutritionRadioGroup, nutritionRadioButtons, nutriHydration);

                String dietTime = carePlan.dietTimings;
                selectRadioButton(dietTimeRadioGroup, dietTimeRadioButtons, dietTime);

                String sleepPattern = carePlan.sleepPattern;
                selectRadioButton(sleepPatternRadioGroup, sleepPatternRadioButtons, sleepPattern);

                String painScore = String.valueOf(carePlan.painScore);
                selectRadioButton(painScoreRadioGroup, painScoreRadioButtons, painScore);

                // Pre-fill support requirements checkboxes
                for (CheckBox checkBox : supportRequirementsCheckBox.keySet()) {
                  String requirement = supportRequirementsCheckBox.get(checkBox);
                  if (carePlan.supportRequirement.contains(requirement)) {
                    checkBox.setChecked(true);
                  }
                }

                // Pre-fill drink likes checkboxes
                for (CheckBox checkBox : drinkLikesCheckBox.keySet()) {
                  String drink = drinkLikesCheckBox.get(checkBox);
                  if (carePlan.drinkLikings.contains(drink)) {
                    checkBox.setChecked(true);
                  }
                }

                // Pre-fill pain checkboxes
                for (CheckBox checkBox : painCheckBox.keySet()) {
                  String pain = painCheckBox.get(checkBox);
                  if (carePlan.painCategories.contains(pain)) {
                    checkBox.setChecked(true);
                  }
                }

                // Pre-fill behavioral management checkboxes
                for (CheckBox checkBox : behavioralManagementCheckBox.keySet()) {
                  String behavior = behavioralManagementCheckBox.get(checkBox);
                  if (carePlan.behavioralManagement.contains(behavior)) {
                    checkBox.setChecked(true);
                  }
                }
              }
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {
            // Handle error
          }
        });
  }

  private String getSelectedRadioButtonValue(
      RadioGroup radioGroup, HashMap<RadioButton, String> radioButtons) {
    int selectedId = radioGroup.getCheckedRadioButtonId();
    if (selectedId != -1) {
      RadioButton radioButton = findViewById(selectedId);
      return radioButtons.get(radioButton);
    }
    return null;
  }

  private void selectRadioButton(
      RadioGroup radioGroup, HashMap<RadioButton, String> radioButtons, String selectedValue) {
    for (RadioButton radioButton : radioButtons.keySet()) {
      if (radioButtons.get(radioButton).equals(selectedValue)) {
        radioGroup.check(radioButton.getId());
        break;
      }
    }
  }

  private CarePlan createUpdatedCarePlan() {
    CarePlan carePlan = new CarePlan();

    // Set care plan type
    carePlan.carePlanType = getSelectedRadioButtonValue(carePlanTypeRadioGroup, carePlanTypesRadioButtons);

    // Set nutrition hydration
    carePlan.nutritionHydration = getSelectedRadioButtonValue(nutritionRadioGroup, nutritionRadioButtons);

    // Set diet timings
    carePlan.dietTimings = getSelectedRadioButtonValue(dietTimeRadioGroup, dietTimeRadioButtons);

    // Set sleep pattern
    carePlan.sleepPattern = getSelectedRadioButtonValue(sleepPatternRadioGroup, sleepPatternRadioButtons);

    // Set pain score
    String painScore = getSelectedRadioButtonValue(painScoreRadioGroup, painScoreRadioButtons);
    if (painScore != null) {
      carePlan.painScore = Integer.parseInt(painScore);
    }

    // Set support requirements checkboxes
    carePlan.supportRequirement = getCheckedItemsAsString(supportRequirementsCheckBox);

    // Set drink likes checkboxes
    carePlan.drinkLikings = getCheckedItemsAsString(drinkLikesCheckBox);

    // Set pain checkboxes
    carePlan.painCategories = getCheckedItemsAsString(painCheckBox);

    // Set behavioral management checkboxes
    carePlan.behavioralManagement = getCheckedItemsAsString(behavioralManagementCheckBox);

    return carePlan;
  }

  private String getCheckedItemsAsString(HashMap<CheckBox, String> checkBoxHashMap) {
    StringBuilder stringBuilder = new StringBuilder();
    for (CheckBox checkBox : checkBoxHashMap.keySet()) {
      if (checkBox.isChecked()) {
        stringBuilder.append(checkBoxHashMap.get(checkBox)).append(", ");
      }
    }
    // Remove trailing comma and space if any
    if (stringBuilder.length() > 0) {
      stringBuilder.setLength(stringBuilder.length() - 2);
    }
    return stringBuilder.toString();
  }

  private void saveCarePlanToFirebase(CarePlan carePlan) {
    // Update care plan in Firebase
    carePlanRef.child(patientId).setValue(carePlan);
  }
}
