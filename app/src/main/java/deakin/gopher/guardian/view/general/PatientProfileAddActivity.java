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
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.PatientProfileAddAdapter;
import deakin.gopher.guardian.model.GP;
import deakin.gopher.guardian.model.MedicalDiagnostic;
import deakin.gopher.guardian.model.NextOfKin;
import deakin.gopher.guardian.model.Patient;
import deakin.gopher.guardian.services.api.ApiClient;
import deakin.gopher.guardian.services.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Saving Changes?");
    builder.setPositiveButton(
            "YES",
            (dialog, whichButton) -> saveInAPI());
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
  }

  private void saveInAPI() {
    ApiService apiService = ApiClient.getApiService();

    // Save Patient
    Call<Void> patientCall = apiService.createPatient(patient);
    patientCall.enqueue(new Callback<Void>() {
      @Override
      public void onResponse(Call<Void> call, Response<Void> response) {
        if (response.isSuccessful()) {
          Toast.makeText(PatientProfileAddActivity.this, "Patient added successfully", Toast.LENGTH_SHORT).show();
          createMedicalDiagnostic(patient.getId()); // Call diagnostic API after success
        } else {
          Toast.makeText(PatientProfileAddActivity.this, "Failed to add patient", Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onFailure(Call<Void> call, Throwable t) {
        Toast.makeText(PatientProfileAddActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void createMedicalDiagnostic(final String patientId) {
    MedicalDiagnostic diagnostic = new MedicalDiagnostic(patientId, true);
    ApiClient.getApiService().createMedicalDiagnostic(diagnostic).enqueue(new Callback<Void>() {
      @Override
      public void onResponse(Call<Void> call, Response<Void> response) {
        if (response.isSuccessful()) {
          Toast.makeText(PatientProfileAddActivity.this, "Medical diagnostic added successfully", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(PatientProfileAddActivity.this, "Failed to add medical diagnostic", Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onFailure(Call<Void> call, Throwable t) {
        Toast.makeText(PatientProfileAddActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }
}
