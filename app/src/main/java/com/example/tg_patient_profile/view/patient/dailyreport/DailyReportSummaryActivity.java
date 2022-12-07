package com.example.tg_patient_profile.view.patient.dailyreport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.util.Util;

import java.text.ParseException;
import java.util.Date;


public class DailyReportSummaryActivity extends AppCompatActivity {

    private String date;
    private String notes;
    private String[] statusList;
    private StringBuilder statuses = new StringBuilder();

    private long dateMs;

    private TextView currentStatusSummary;
    private TextView progressNotesSummary;
    private CalendarView patientReportSummaryCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report_summary);

        currentStatusSummary = (TextView) findViewById(R.id.currentStatusSummary);
        progressNotesSummary = (TextView) findViewById(R.id.progressNotesSummary);
        patientReportSummaryCalendarView = (CalendarView) findViewById(R.id.patientReportSummaryCalendarView);

        Intent intent = getIntent();
        date = intent.getStringExtra(Util.DAILY_REPORT_DATE);
        notes = intent.getStringExtra(Util.DAILY_REPORT_STATUS_NOTES);
        statusList = intent.getStringArrayExtra(Util.DAILY_REPORT_STATUS_LIST);
        if (statusList.length != 0) {
            for (int i = 0; i < statusList.length - 1; i++) {
                statuses.append(statusList[i] + "\n");
            }
            statuses.append(statusList[statusList.length-1]);
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Date d = null;
                d = formatter.parse(date);
                dateMs = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        progressNotesSummary.setText(notes);
        currentStatusSummary.setText(statuses);
        patientReportSummaryCalendarView.setDate(dateMs);
    }
}