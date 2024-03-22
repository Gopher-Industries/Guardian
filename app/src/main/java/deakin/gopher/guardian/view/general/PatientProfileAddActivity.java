package deakin.gopher.guardian.view.general;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.PatientProfileAddAdapter;
import deakin.gopher.guardian.model.GP;
import deakin.gopher.guardian.model.MedicalDiagnostic;
import deakin.gopher.guardian.model.NextOfKin;
import deakin.gopher.guardian.model.Patient;
import deakin.gopher.guardian.util.DataListener;

public class PatientProfileAddActivity extends BaseActivity implements DataListener {

  private CustomHeader customHeader;
  private Patient patient;
  private NextOfKin nextOfKin1, nextOfKin2;
  private GP gp1, gp2;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_patient_profile_add);

    final ViewPager2 viewPager2 = findViewById(R.id.dataForViewViewPager);
    customHeader = findViewById(R.id.customHeader);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    final NavigationView navigationView = findViewById(R.id.nav_view);

    final PatientProfileAddAdapter viewPagerAdapter =
        new PatientProfileAddAdapter(getSupportFragmentManager(), getLifecycle());
    viewPager2.setAdapter(viewPagerAdapter);

    customHeader.setHeaderHeight(450);
    customHeader.setHeaderText("Patient Add");
    customHeader.setHeaderTopImageVisibility(View.VISIBLE);
    customHeader.setHeaderTopImage(R.drawable.add_image_button);

    navigationView.setItemIconTintList(null);

    if (null != customHeader) {
      customHeader.menuButton.setOnClickListener(
          v -> {
            if (null != drawerLayout) {
              drawerLayout.openDrawer(GravityCompat.START);
            }
          });
    }

    viewPager2.registerOnPageChangeCallback(
        new ViewPager2.OnPageChangeCallback() {
          @Override
          public void onPageSelected(final int position) {
            switch (position) {
              case 0:
                customHeader.setHeaderText("Add New Patient");
                break;
              case 1:
                customHeader.setHeaderText("Add Next of Kin");
                break;
              case 2:
                customHeader.setHeaderText("Add Next of Kin+");
                break;
              case 3:
                customHeader.setHeaderText("Add General Practitioner");
                break;
              default:
                customHeader.setHeaderText("Add General Practitioner+");
                break;
            }
          }
        });
  }

  @Override
  public void onDataFilled(
      final Patient patient,
      final NextOfKin nextOfKin1,
      final NextOfKin nextOfKin2,
      final GP gp1,
      final GP gp2) {

    if (null != patient) {
      this.patient = patient;
    }

    if (null != nextOfKin1) {
      this.nextOfKin1 = nextOfKin1;
    }

    if (null != nextOfKin2) {
      this.nextOfKin2 = nextOfKin2;
    }

    if (null != gp1) {
      this.gp1 = gp1;
    }

    if (null != gp2) {
      this.gp2 = gp2;
    }
  }

  @Override
  public void onDataFinished(final Boolean isFinished) {
    // insert a dialog here

    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Saving Changes?");
    builder.setPositiveButton(
        "YES",
        (dialog, whichButton) -> {
          saveInFirebase();
          // Toast.makeText(MainActivity.this, "Yaay", Toast.LENGTH_SHORT).show();
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
          // dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
        });
    dialog.show();
    // insert

  }

  private void saveInFirebase() {
    final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference patientRef = databaseRef.child("patient_profile");
    final DatabaseReference nokRef = databaseRef.child("next_of_kins_profile");
    final DatabaseReference gpRef = databaseRef.child("gp_profile");
    final String nof1_id = nokRef.push().getKey();
    final String nof2_id = nokRef.push().getKey();
    final String gp1_id = gpRef.push().getKey();
    final String gp2_id = gpRef.push().getKey();
    patient.setNokId1(nof1_id);
    patient.setNokId2(nof2_id);
    patient.setGpId1(gp1_id);
    patient.setGpId2(gp2_id);
    nokRef
        .child(nof1_id)
        .setValue(nextOfKin1)
        .addOnSuccessListener(unused -> Log.v("nok1 success", ""))
        .addOnFailureListener(
            e ->
                Toast.makeText(
                        PatientProfileAddActivity.this,
                        "fail to upload first next of kin" + e.getMessage(),
                        Toast.LENGTH_SHORT)
                    .show());

    nokRef
        .child(nof2_id)
        .setValue(nextOfKin2)
        .addOnSuccessListener(unused -> Log.v("nok2success", ""))
        .addOnFailureListener(
            e ->
                Toast.makeText(
                        PatientProfileAddActivity.this,
                        "fail to upload second next of kin" + e.getMessage(),
                        Toast.LENGTH_SHORT)
                    .show());

    gpRef
        .child(gp1_id)
        .setValue(gp1)
        .addOnSuccessListener(unused -> Log.v("gp1success", ""))
        .addOnFailureListener(
            e ->
                Toast.makeText(
                        PatientProfileAddActivity.this,
                        "fail to upload first gp" + e.getMessage(),
                        Toast.LENGTH_SHORT)
                    .show());
    if (null != gp2) {

      gpRef
          .child(gp2_id)
          .setValue(gp2)
          .addOnSuccessListener(unused -> Log.v("gp2success", ""))
          .addOnFailureListener(
              e ->
                  Toast.makeText(
                          PatientProfileAddActivity.this,
                          "fail to upload second gp" + e.getMessage(),
                          Toast.LENGTH_SHORT)
                      .show());
    }

    final String patient_id = patientRef.push().getKey();
    patientRef
        .child(patient_id)
        .setValue(patient)
        .addOnSuccessListener(
            unused -> {
              Toast.makeText(
                      PatientProfileAddActivity.this,
                      "Success on adding a new patient",
                      Toast.LENGTH_SHORT)
                  .show();
              final Intent intent =
                  new Intent(PatientProfileAddActivity.this, PatientProfileActivity.class);
              intent.putExtra("created_patient_id", patient_id);
              startActivity(intent);
            })
        .addOnFailureListener(
            e ->
                Toast.makeText(
                        PatientProfileAddActivity.this,
                        "fail to upload patient" + e.getMessage(),
                        Toast.LENGTH_SHORT)
                    .show());
    PatientRelatedCreat(patient_id);
  }

  private void PatientRelatedCreat(final String patient_id) {
    createMedicalDiagnostic(patient_id);
  }

  private void createMedicalDiagnostic(final String patient_id) {
    final MedicalDiagnostic current_medical_diagnostic = new MedicalDiagnostic(patient_id, true);
    final DatabaseReference reference =
        FirebaseDatabase.getInstance().getReference("health_details");
    final String id = reference.push().getKey();
    reference
        .child(id)
        .setValue(current_medical_diagnostic)
        .addOnFailureListener(
            e ->
                Toast.makeText(
                        PatientProfileAddActivity.this,
                        "Fail to create health detail of this patient!Please try it again!Reason:"
                            + e.getMessage(),
                        Toast.LENGTH_SHORT)
                    .show());
  }
}
