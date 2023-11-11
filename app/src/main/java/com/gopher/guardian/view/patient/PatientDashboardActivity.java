package com.gopher.guardian.view.patient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.gopher.guardian.R;
import com.gopher.guardian.util.Util;
import com.gopher.guardian.view.general.DrawerActivity;
import com.gopher.guardian.view.general.PatientListActivity;
import com.gopher.guardian.view.patient.associateradar.ActivityProfilingActivity;
import com.gopher.guardian.view.patient.careplan.CarePlanActivity;
import com.gopher.guardian.view.patient.dailyreport.DailyReportActivity;
import com.gopher.guardian.view.patient.dailyreport.DailyReportSummaryActivity;
import com.gopher.guardian.view.patient.patientdata.healthdata.HealthDataActivity;
import com.gopher.guardian.view.patient.patientdata.medicaldiagnostics.MedicalDiagnosticsActivity;
import com.gopher.guardian.view.patient.viewactivitydata.WeeklyActivityProfilingActivity;
import java.util.HashSet;
import java.util.Set;

public class PatientDashboardActivity extends AppCompatActivity {

  ImageButton dailyReportButton;
  ImageButton healthDataButton;
  ImageButton viewActivityDataButton;
  ImageButton associateRadarButton;
  TextView choosePatientButton;
  Boolean dailyReportLodged;
  Set<String> dailyReportStatusList = new HashSet<>();
  String dailyReportNotes, dailyReportDate;

  @Override
  protected void onResume() {
    super.onResume();
    final SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
    dailyReportLodged = prefs.getBoolean(Util.DAILY_REPORT_LODGED, false);
    dailyReportStatusList =
        prefs.getStringSet(Util.DAILY_REPORT_STATUS_LIST, dailyReportStatusList);
    dailyReportNotes = prefs.getString(Util.DAILY_REPORT_STATUS_NOTES, "");
    dailyReportDate = prefs.getString(Util.DAILY_REPORT_DATE, "");
  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_patient_dashboard);

    choosePatientButton = findViewById(R.id.selectAPatientTextView);
    dailyReportButton = findViewById(R.id.dailyReportButton);
    healthDataButton = findViewById(R.id.healthDataButton);
    viewActivityDataButton = findViewById(R.id.viewActivityDataButton);
    associateRadarButton = findViewById(R.id.associateRadarButton);

    dailyReportButton.setOnClickListener(
        v -> {
          if (!dailyReportLodged) {
            final Intent dailyReportIntent =
                new Intent(PatientDashboardActivity.this, DailyReportActivity.class);
            startActivity(dailyReportIntent);
          } else {
            final Intent dailyReportSummaryIntent =
                new Intent(PatientDashboardActivity.this, DailyReportSummaryActivity.class);
            dailyReportSummaryIntent.putExtra(Util.DAILY_REPORT_STATUS_NOTES, dailyReportNotes);
            dailyReportSummaryIntent.putExtra(Util.DAILY_REPORT_DATE, dailyReportDate);
            dailyReportSummaryIntent.putExtra(
                Util.DAILY_REPORT_STATUS_LIST, Util.setToArray(dailyReportStatusList));
            Log.i("berapa", Util.setToArray(dailyReportStatusList).length + "");
            startActivity(dailyReportSummaryIntent);
          }
        });

    healthDataButton.setOnClickListener(
        v -> {
          final Intent patientListIntent =
              new Intent(PatientDashboardActivity.this, HealthDataActivity.class);
          startActivity(patientListIntent);
        });

    choosePatientButton.setOnClickListener(
        v -> {
          final Intent patientListIntent =
              new Intent(PatientDashboardActivity.this, PatientListActivity.class);
          startActivity(patientListIntent);
        });

    viewActivityDataButton.setOnClickListener(
        v -> {
          final Intent weeklyActivityProfilingIntent =
              new Intent(PatientDashboardActivity.this, WeeklyActivityProfilingActivity.class);
          startActivity(weeklyActivityProfilingIntent);
        });
    associateRadarButton.setOnClickListener(
        v -> {
          final Intent activityProfilingIntent =
              new Intent(PatientDashboardActivity.this, ActivityProfilingActivity.class);
          startActivity(activityProfilingIntent);
        });
  }

  public void onNavigationDrawerClick(final View view) {
    final Intent drawerActivityActivityIntent =
        new Intent(PatientDashboardActivity.this, DrawerActivity.class);
    startActivity(drawerActivityActivityIntent);
  }

  public void onMedicalDiagnosticsClick(final View view) {
    final Intent patientProfileUpdateIntent =
        new Intent(PatientDashboardActivity.this, MedicalDiagnosticsActivity.class);
    startActivity(patientProfileUpdateIntent);
  }

  public void onCarePlanClick(final View view) {
    final Intent patientProfileUpdateIntent =
        new Intent(PatientDashboardActivity.this, CarePlanActivity.class);
    startActivity(patientProfileUpdateIntent);
  }
}
