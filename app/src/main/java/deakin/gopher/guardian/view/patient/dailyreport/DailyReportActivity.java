package deakin.gopher.guardian.view.patient.dailyreport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.login.Role;
import deakin.gopher.guardian.model.login.SessionManager;
import deakin.gopher.guardian.services.NavigationService;
import deakin.gopher.guardian.util.Util;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class DailyReportActivity extends AppCompatActivity {

  private final Calendar selectedDate = Calendar.getInstance();
  private final Set<String> selectedStatuses = new LinkedHashSet<>();
  private ImageView dailyReportMenuButton;
  private Spinner daySpinner;
  private Spinner monthSpinner;
  private Spinner yearSpinner;
  private Button todayButton;
  private Button submitButton;
  private EditText progressNotesEditText;
  private TextView urgentMedicalAttentionTextView;
  private TextView requiresHospitalisationTextView;
  private TextView notApplicableTextView;
  private TextView requiresHourlyAttentionTextView;
  private TextView usernameTextView;
  private TextView reportedByTextView;
  private String patientName;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_daily_report);

    final NavigationView navigationView = findViewById(R.id.nav_view);
    dailyReportMenuButton = findViewById(R.id.menuButton11);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    final NavigationService navigationService = new NavigationService(this);
    final boolean canAddTasks =
        SessionManager.INSTANCE.getCurrentUser().getRole() instanceof Role.Caretaker;
    navigationView.getMenu().findItem(R.id.add_task).setVisible(canAddTasks);

    navigationView.setNavigationItemSelectedListener(
        menuItem -> {
          final int id = menuItem.getItemId();
          if (R.id.nav_home == id) {
            navigationService.toHomeScreenForRole(
                SessionManager.INSTANCE.getCurrentUser().getRole());
          } else if (R.id.add_task == id && canAddTasks) {
            navigationService.onLaunchTaskCreator();
          } else if (R.id.nav_signout == id) {
            navigationService.onSignOut();
            finish();
          }
          drawerLayout.closeDrawer(GravityCompat.START);
          return true;
        });

    dailyReportMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });

    final String patientNameExtra = getIntent().getStringExtra("patientName");
    patientName =
        patientNameExtra != null && !patientNameExtra.trim().isEmpty()
            ? patientNameExtra.split(" ")[0]
            : "Patient";

    usernameTextView = findViewById(R.id.username);
    reportedByTextView = findViewById(R.id.save);
    daySpinner = findViewById(R.id.day);
    monthSpinner = findViewById(R.id.month);
    yearSpinner = findViewById(R.id.year);
    todayButton = findViewById(R.id.today_button);
    submitButton = findViewById(R.id.loginBtn);
    progressNotesEditText = findViewById(R.id.patientNnormal);
    urgentMedicalAttentionTextView = findViewById(R.id.urgentMedicalAttentionTextView);
    requiresHospitalisationTextView = findViewById(R.id.requiresHospitalisationTextView);
    notApplicableTextView = findViewById(R.id.notApplicableTextView);
    requiresHourlyAttentionTextView = findViewById(R.id.requiresHourlyAttentionTextView);

    usernameTextView.setText(patientName);
    reportedByTextView.setText(SessionManager.INSTANCE.getCurrentUser().getName());

    setupDateSpinners();
    applyDateToSpinners();

    todayButton.setOnClickListener(
        v -> {
          selectedDate.setTimeInMillis(System.currentTimeMillis());
          applyDateToSpinners();
        });

    bindStatusToggle(urgentMedicalAttentionTextView);
    bindStatusToggle(requiresHospitalisationTextView);
    bindStatusToggle(notApplicableTextView);
    bindStatusToggle(requiresHourlyAttentionTextView);

    submitButton.setOnClickListener(v -> submitDailyReport());
  }

  private void setupDateSpinners() {
    final Calendar today = Calendar.getInstance();
    final ArrayList<String> days = new ArrayList<>();
    final ArrayList<String> months = new ArrayList<>();
    final ArrayList<String> years = new ArrayList<>();

    for (int day = 1; day <= 31; day++) {
      days.add(String.format(Locale.getDefault(), "%02d", day));
    }
    for (int month = 1; month <= 12; month++) {
      months.add(String.format(Locale.getDefault(), "%02d", month));
    }
    for (int year = today.get(Calendar.YEAR) - 1; year <= today.get(Calendar.YEAR) + 3; year++) {
      years.add(String.valueOf(year));
    }

    daySpinner.setAdapter(
        new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days));
    monthSpinner.setAdapter(
        new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months));
    yearSpinner.setAdapter(
        new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years));
  }

  private void applyDateToSpinners() {
    daySpinner.setSelection(selectedDate.get(Calendar.DAY_OF_MONTH) - 1);
    monthSpinner.setSelection(selectedDate.get(Calendar.MONTH));
    yearSpinner.setSelection(findYearPosition(selectedDate.get(Calendar.YEAR)));
  }

  private int findYearPosition(final int year) {
    for (int i = 0; i < yearSpinner.getCount(); i++) {
      if (String.valueOf(year).equals(String.valueOf(yearSpinner.getItemAtPosition(i)))) {
        return i;
      }
    }
    return 0;
  }

  private void bindStatusToggle(final TextView statusView) {
    updateStatusSelection(statusView, false);
    statusView.setOnClickListener(
        v -> {
          final String status = statusView.getText().toString();
          final boolean isSelected;
          if (selectedStatuses.contains(status)) {
            selectedStatuses.remove(status);
            isSelected = false;
          } else {
            selectedStatuses.add(status);
            isSelected = true;
          }
          updateStatusSelection(statusView, isSelected);
        });
  }

  private void updateStatusSelection(final TextView statusView, final boolean isSelected) {
    statusView.setTextColor(
        ContextCompat.getColor(this, isSelected ? R.color.TG_blue : R.color.default_text));
    statusView.setAlpha(isSelected ? 1f : 0.75f);
  }

  private void submitDailyReport() {
    if (selectedStatuses.isEmpty()) {
      Toast.makeText(this, "Select at least one alert status", Toast.LENGTH_SHORT).show();
      return;
    }

    final String date =
        String.format(
            Locale.getDefault(),
            "%s/%s/%s",
            daySpinner.getSelectedItem(),
            monthSpinner.getSelectedItem(),
            yearSpinner.getSelectedItem());
    final String notes = progressNotesEditText.getText().toString().trim();
    final String[] statuses = selectedStatuses.toArray(new String[0]);

    final SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
    prefs
        .edit()
        .putBoolean(Util.DAILY_REPORT_LODGED, true)
        .putString(Util.DAILY_REPORT_DATE, date)
        .putString(Util.DAILY_REPORT_STATUS_NOTES, notes)
        .putStringSet(Util.DAILY_REPORT_STATUS_LIST, new LinkedHashSet<>(selectedStatuses))
        .apply();

    final Intent dailyReportSummaryIntent =
        new Intent(DailyReportActivity.this, DailyReportSummaryActivity.class);
    dailyReportSummaryIntent.putExtra(Util.DAILY_REPORT_STATUS_NOTES, notes);
    dailyReportSummaryIntent.putExtra(Util.DAILY_REPORT_DATE, date);
    dailyReportSummaryIntent.putExtra(Util.DAILY_REPORT_STATUS_LIST, statuses);
    dailyReportSummaryIntent.putExtra("patientName", patientName);
    startActivity(dailyReportSummaryIntent);
  }
}
