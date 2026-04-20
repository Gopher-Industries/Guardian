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

        // ADDED: Keep original menu icon colours
        navigationView.setItemIconTintList(null);

        // Open drawer when menu button is clicked
        dailyReportMenuButton.setOnClickListener(
                v -> drawerLayout.openDrawer(GravityCompat.START));

        // Get patient data from intent
        String patientNameExtra = getIntent().getStringExtra("patientName");
        patientId = getIntent().getStringExtra("patientId");

        // ADDED: Safe handling for null or empty patient name
        if (patientNameExtra != null && !patientNameExtra.isEmpty()) {
            patientName = patientNameExtra.split(" ")[0];
        } else {
            patientName = "Patient";
        }

        usernameTextView.setText(patientName);

        // ADDED: Dummy frontend data for now
        reportedByTextView.setText("Nurse John");
        progressNotesEditText.setText("Patient is stable and doing well.");

        // ADDED: Default selected alert
        highlightSelectedAlert(notApplicableTextView);

        // ADDED: Alert selection handling
        urgentMedicalAttentionTextView.setOnClickListener(v -> {
            selectedAlert = "Urgent Medical Attention";
            highlightSelectedAlert(urgentMedicalAttentionTextView);
            showToast(selectedAlert);
        });

        requiresHospitalisationTextView.setOnClickListener(v -> {
            selectedAlert = "Requires Hospitalisation";
            highlightSelectedAlert(requiresHospitalisationTextView);
            showToast(selectedAlert);
        });

        notApplicableTextView.setOnClickListener(v -> {
            selectedAlert = "Not Applicable";
            highlightSelectedAlert(notApplicableTextView);
            showToast(selectedAlert);
        });

        requiresHourlyAttentionTextView.setOnClickListener(v -> {
            selectedAlert = "Requires Hourly Attention";
            highlightSelectedAlert(requiresHourlyAttentionTextView);
            showToast(selectedAlert);
        });

        // ADDED: Basic validation before submission
        submitButton.setOnClickListener(v -> {
            String notes = progressNotesEditText.getText().toString().trim();

            if (notes.isEmpty()) {
                Toast.makeText(this, "Please enter progress notes", Toast.LENGTH_SHORT).show();
            } else {
                // IMPROVED: Show submitted values for demo feedback
                Toast.makeText(
                        this,
                        "Daily Report Submitted\nAlert: " + selectedAlert,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // ADDED: Navigation handling using actual menu ids
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Toast.makeText(this, "Going to Home", Toast.LENGTH_SHORT).show();

                // Uncomment when your Home activity is ready
                // Intent intent = new Intent(DailyReportActivity.this, PatientHomeActivity.class);
                // intent.putExtra("patientId", patientId);
                // intent.putExtra("patientName", patientNameExtra);
                // startActivity(intent);

            } else if (id == R.id.add_task) {
                Toast.makeText(this, "Going to Add Task", Toast.LENGTH_SHORT).show();

                // Uncomment when your Add Task activity is ready
                // Intent intent = new Intent(DailyReportActivity.this, TaskAddActivity.class);
                // intent.putExtra("patientId", patientId);
                // intent.putExtra("patientName", patientNameExtra);
                // startActivity(intent);

            } else if (id == R.id.nav_signout) {
                // ADDED: Placeholder sign out behaviour
                Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();

                // Add real sign out logic here later
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    // ADDED: Reusable toast method for cleaner code
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // ADDED: Simple visual feedback for selected alert
    private void highlightSelectedAlert(TextView selectedTextView) {
        urgentMedicalAttentionTextView.setAlpha(0.6f);
        requiresHospitalisationTextView.setAlpha(0.6f);
        notApplicableTextView.setAlpha(0.6f);
        requiresHourlyAttentionTextView.setAlpha(0.6f);

        selectedTextView.setAlpha(1.0f);
    }
}