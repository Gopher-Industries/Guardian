package deakin.gopher.guardian.view.patient.dailyreport;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.view.general.Homepage4admin;
import deakin.gopher.guardian.view.general.Homepage4caretaker;
import deakin.gopher.guardian.view.general.LoginActivity;

public class DailyReportActivity extends AppCompatActivity {

  ImageView dailyReportMenuButton;

  DrawerLayout drawerLayout;
  NavigationView navigationView;

  TextView usernameTextView;
  TextView reportedByTextView;

  EditText progressNotesEditText;

  TextView urgentMedicalAttentionTextView;
  TextView requiresHospitalisationTextView;
  TextView notApplicableTextView;
  TextView requiresHourlyAttentionTextView;

  Button submitButton;

  String patientId;
  String patientName;
  String selectedAlert = "Not Applicable";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_daily_report);

    drawerLayout = findViewById(R.id.drawer_layout);
    navigationView = findViewById(R.id.nav_view);
    dailyReportMenuButton = findViewById(R.id.menuButton11);

    usernameTextView = findViewById(R.id.username);
    reportedByTextView = findViewById(R.id.save);
    progressNotesEditText = findViewById(R.id.patientNnormal);

    urgentMedicalAttentionTextView = findViewById(R.id.urgentMedicalAttentionTextView);
    requiresHospitalisationTextView = findViewById(R.id.requiresHospitalisationTextView);
    notApplicableTextView = findViewById(R.id.notApplicableTextView);
    requiresHourlyAttentionTextView = findViewById(R.id.requiresHourlyAttentionTextView);

    submitButton = findViewById(R.id.loginBtn);

    navigationView.setItemIconTintList(null);

    dailyReportMenuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

    String patientNameExtra = getIntent().getStringExtra("patientName");
    patientId = getIntent().getStringExtra("patientId");

    if (patientNameExtra != null && !patientNameExtra.isEmpty()) {
      patientName = patientNameExtra.split(" ")[0];
    } else {
      patientName = "Patient";
    }

    usernameTextView.setText(patientName);

    reportedByTextView.setText("Nurse John");
    progressNotesEditText.setText("Patient is stable and doing well.");

    highlightSelectedAlert(notApplicableTextView);

    urgentMedicalAttentionTextView.setOnClickListener(
        v -> {
          selectedAlert = "Urgent Medical Attention";
          highlightSelectedAlert(urgentMedicalAttentionTextView);
          showToast(selectedAlert);
        });

    requiresHospitalisationTextView.setOnClickListener(
        v -> {
          selectedAlert = "Requires Hospitalisation";
          highlightSelectedAlert(requiresHospitalisationTextView);
          showToast(selectedAlert);
        });

    notApplicableTextView.setOnClickListener(
        v -> {
          selectedAlert = "Not Applicable";
          highlightSelectedAlert(notApplicableTextView);
          showToast(selectedAlert);
        });

    requiresHourlyAttentionTextView.setOnClickListener(
        v -> {
          selectedAlert = "Requires Hourly Attention";
          highlightSelectedAlert(requiresHourlyAttentionTextView);
          showToast(selectedAlert);
        });

    submitButton.setOnClickListener(
        v -> {
          String notes = progressNotesEditText.getText().toString().trim();

          if (notes.isEmpty()) {
            Toast.makeText(this, "Please enter progress notes", Toast.LENGTH_SHORT).show();
          } else {
            Toast.makeText(
                    this, "Daily Report Submitted\nAlert: " + selectedAlert, Toast.LENGTH_SHORT)
                .show();
          }
        });

    navigationView.setNavigationItemSelectedListener(
        menuItem -> {
          Intent intent = null;
          final int itemId = menuItem.getItemId();

          if (itemId == R.id.nav_home) {
            final boolean isAdmin =
                deakin.gopher.guardian.model.login.SessionManager.INSTANCE
                        .getCurrentUser()
                        .getRole()
                    instanceof deakin.gopher.guardian.model.login.Role.Admin;
            intent =
                new Intent(
                    DailyReportActivity.this,
                    isAdmin ? Homepage4admin.class : Homepage4caretaker.class);
          } else if (itemId == R.id.nav_signout) {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
            intent = new Intent(DailyReportActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
          }

          if (intent != null) {
            startActivity(intent);
          }

          if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
          }
          return true;
        });
  }

  private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  private void highlightSelectedAlert(TextView selectedTextView) {
    urgentMedicalAttentionTextView.setAlpha(0.6f);
    requiresHospitalisationTextView.setAlpha(0.6f);
    notApplicableTextView.setAlpha(0.6f);
    requiresHourlyAttentionTextView.setAlpha(0.6f);

    selectedTextView.setAlpha(1.0f);
  }
}
