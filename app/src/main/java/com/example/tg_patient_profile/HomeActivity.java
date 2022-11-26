package com.example.tg_patient_profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    ImageButton newPatientButton, dailyReportButton, editPatientDataButton,
            patientListButton, viewActivityDataButton, associateRadarButton,
    newUserButton, settingsButton, signOutButton;
    Boolean dailyReportLodged;
    Set<String> dailyReportStatusList = new HashSet<>();
    String dailyReportNotes, dailyReportDate;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
        dailyReportLodged = prefs.getBoolean(Util.DAILY_REPORT_LODGED, false);
        dailyReportStatusList = prefs.getStringSet(Util.DAILY_REPORT_STATUS_LIST, dailyReportStatusList);
        Log.i("berapa", dailyReportStatusList.size() + "");
        dailyReportNotes = prefs.getString(Util.DAILY_REPORT_STATUS_NOTES, "");
        dailyReportDate = prefs.getString(Util.DAILY_REPORT_DATE, "");
        Log.i("mok", "onResume: " + dailyReportLodged);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        newPatientButton = (ImageButton) findViewById(R.id.NewPatientButton);
        dailyReportButton = (ImageButton) findViewById(R.id.DailyReportButton);
        editPatientDataButton = (ImageButton) findViewById(R.id.EditPatientDataButton);
        patientListButton = (ImageButton) findViewById(R.id.PatientListButton);
        viewActivityDataButton = (ImageButton) findViewById(R.id.ViewActivityDataButton);
        associateRadarButton = (ImageButton) findViewById(R.id.AssociateRadarButton);
        newUserButton = (ImageButton) findViewById(R.id.NewUserButton);
        settingsButton = (ImageButton) findViewById(R.id.SettingsButton);
        signOutButton = (ImageButton) findViewById(R.id.SignOutButton);

        Log.i("mok", "onCreate: " + dailyReportLodged);

        newPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPatientIntent = new Intent(HomeActivity.this, NewPatientActivity.class);
                startActivity(newPatientIntent);
            }
        });

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

        editPatientDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dailyReportIntent = new Intent(HomeActivity.this, TestActivity.class);
                startActivity(dailyReportIntent);
            }
        });

        patientListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent patientProfileList = new Intent(HomeActivity.this, PatientProfileList.class);
                startActivity(patientProfileList);
            }
        });

        viewActivityDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        associateRadarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}