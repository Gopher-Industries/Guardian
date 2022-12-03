//Testing - Home Page

package com.example.tg_patient_profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    ImageButton patientDataButton, dailyReportButton, healthDataButton,
            viewActivityDataButton, associateRadarButton, carePlanButton;
    TextView choosePatientButton;
    Boolean dailyReportLodged;
    Set<String> dailyReportStatusList = new HashSet<>();
    String dailyReportNotes, dailyReportDate;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
        dailyReportLodged = prefs.getBoolean(Util.DAILY_REPORT_LODGED, false);
        dailyReportStatusList = prefs.getStringSet(Util.DAILY_REPORT_STATUS_LIST, dailyReportStatusList);
        dailyReportNotes = prefs.getString(Util.DAILY_REPORT_STATUS_NOTES, "");
        dailyReportDate = prefs.getString(Util.DAILY_REPORT_DATE, "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        choosePatientButton = (TextView) findViewById(R.id.chooseDiffPatientTV);
        patientDataButton = (ImageButton) findViewById(R.id.patientDataButton);
        dailyReportButton = (ImageButton) findViewById(R.id.dailyReportButton);
        healthDataButton = (ImageButton) findViewById(R.id.healthDataButton);
        viewActivityDataButton = (ImageButton) findViewById(R.id.viewActivityDataButton);
        associateRadarButton = (ImageButton) findViewById(R.id.associateRadarButton);
        carePlanButton = (ImageButton) findViewById(R.id.carePlanButton);

        dailyReportButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!dailyReportLodged) {
                    Intent dailyReportIntent = new Intent(HomeActivity.this, DailyReportActivity.class);
                    startActivity(dailyReportIntent);
                } else {
                    Intent dailyReportSummaryIntent = new Intent(HomeActivity.this, DailyReportSummaryActivity.class);
                    dailyReportSummaryIntent.putExtra(Util.DAILY_REPORT_STATUS_NOTES, dailyReportNotes);
                    dailyReportSummaryIntent.putExtra(Util.DAILY_REPORT_DATE, dailyReportDate);
                    dailyReportSummaryIntent.putExtra(Util.DAILY_REPORT_STATUS_LIST, Util.setToArray(dailyReportStatusList));
                    Log.i("berapa", Util.setToArray(dailyReportStatusList).length + "");
                    startActivity(dailyReportSummaryIntent);
                }
            }
        });

        carePlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent carePlanIntent = new Intent(HomeActivity.this, CarePlanActivity.class);
                startActivity(carePlanIntent);
            }
        });

        choosePatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent patientListIntent = new Intent(HomeActivity.this, PatientProfileList.class);
                startActivity(patientListIntent);
            }
        });

        viewActivityDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weeklyActivityProfilingIntent = new Intent(HomeActivity.this, WeeklyActivityProfilingActivity.class);
                startActivity(weeklyActivityProfilingIntent);
            }
        });

        associateRadarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityProfilingIntent = new Intent(HomeActivity.this, ActivityProfilingActivity.class);
                startActivity(activityProfilingIntent);
            }
        });
    }


    //Listener for the button to add orders
    public void onMedicalDiagnosticsClick(View view) {
        Intent medicalDiagnosticsActivityIntent = new Intent(HomeActivity.this, MedicalDiagnosticsActivity.class);
        startActivity(medicalDiagnosticsActivityIntent);
    }

    //Listener for the button to add orders
    public void onNavigationDrawerClick(View view) {
        Intent drawerActivityActivityIntent = new Intent(HomeActivity.this, DrawerActivity.class);
        startActivity(drawerActivityActivityIntent);
    }

    //Listener for the button to add orders
    public void onPatientProfileUpdateClick(View view) {
        Intent patientProfileUpdateIntent = new Intent(HomeActivity.this, PatientProfileUpdate.class);
        startActivity(patientProfileUpdateIntent);
    }
}
